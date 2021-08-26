package com.bdca.sense.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.enums.ErrorInfo;
import com.bdca.sense.entity.Rect;
import com.bdca.sense.entity.SenseDTO;
import com.bdca.sense.manager.FaceEngineManager;
import com.bdca.sense.service.FaceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = { "" }, produces = "application/json;charset=UTF-8")
@Api(description = "sense接口")
public class SenseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(SenseController.class);

	@Autowired
	FaceService faceService;
	@Autowired
	FaceEngineManager faceEngineManager;

	@ResponseBody
	@GetMapping(value = "/sense")
	@ApiOperation(value = "状态", notes = "", response = SenseDTO.class)
	public Object getSense() throws IOException {

		Map<String, Object> health = new HashMap<String, Object>(1);
		health.put("health", false);

		boolean ok = !(faceEngineManager.getErrorCode() != ErrorInfo.MOK.getValue()
				&& faceEngineManager.getErrorCode() != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue());
		health.put("health", ok);

		return health;
	}

	@ResponseBody
	@PutMapping(value = "/sense")
	@ApiOperation(value = "感知测试", notes = "", response = SenseDTO.class)
	public Object putSense(SenseDTO sense, MultipartFile image) throws IOException {
		try {
			if (image != null) {
				sense.setBases64(Base64.encodeBase64String(image.getBytes()));
			}
			return postSense(sense);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
		}
	}

	@ResponseBody
	@PostMapping(value = "/sense")
	@ApiOperation(value = "感知接口", notes = "", response = SenseDTO.class)
	public Object postSense(@RequestBody SenseDTO sense) throws IOException {
		try {
			if (sense.getBases64() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("bases64 required"));
			}

			byte[] bytes = Base64.decodeBase64(sense.getBases64());
			InputStream sbs = new ByteArrayInputStream(bytes);

			if (sbs == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("bases64 required"));
			}

			// Options
			if (sense.getOptions() != null && sense.getOptions().containsKey("mode")) {
				String mode = String.valueOf(sense.getOptions().get("mode"));
				if ("IMAGE".equals(mode.toUpperCase())) {
					faceEngineManager.setFaceEngine(faceEngineManager.getFaceEngineImage());
				} else if ("VIDEO".equals(mode.toUpperCase())) {
					faceEngineManager.setFaceEngine(faceEngineManager.getFaceEngineVideo());
				}
			}

			long start = System.currentTimeMillis();
			List<FaceInfo> faceInfoList = faceService.detectFaces(sbs);
			sense.setTaken((int) (System.currentTimeMillis() - start));

			int maxFaceId = -1;
			// ↓ 在图像上绘制人脸框
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
			for (FaceInfo face : faceInfoList) {
				int top = face.getRect().getTop() <= 0 ? 1 : face.getRect().getTop();
				int left = face.getRect().getLeft() <= 0 ? 1 : face.getRect().getLeft();
				int bottom = face.getRect().getBottom() >= image.getHeight() ? image.getHeight() - 1
						: face.getRect().getBottom();
				int right = face.getRect().getRight() >= image.getWidth() ? image.getWidth() - 1
						: face.getRect().getRight();
				for (int i = left; i < right; i++) {
					image.setRGB(i, top, 0x0000FF);
					image.setRGB(i, bottom, 0x0000FF);
				}
				for (int i = top; i < bottom; i++) {
					image.setRGB(left, i, 0x0000FF);
					image.setRGB(right, i, 0x0000FF);
				}

				// 绘制FaceId
				int faceId = face.getFaceId();
				if (faceId >= 0) {

					// 更新最新的faceId
					maxFaceId = maxFaceId < faceId ? faceId : maxFaceId;

					String s = String.valueOf(faceId);
					// 获取画布的画笔
					Graphics2D g2 = (Graphics2D) image.getGraphics();
					// 开始绘图
					FontRenderContext context = g2.getFontRenderContext();
					Font font = new Font("黑体", Font.BOLD, 40);
					Rectangle2D bounds = font.getStringBounds(s, context);
					int x = left + 1;
					int y = (int) (top + bounds.getHeight() / 4);
					// 绘制字符串
					g2.setFont(font);
					g2.setPaint(Color.blue);
					g2.drawString(s, x, y);
					g2.dispose();
				}
			}
			// ↑ 在图像上绘制人脸框

			// ↓ 只保留在boxes范围内的的对象
			List<FaceInfo> faceInfoListRazor = null;
			if (sense.getBoxes() != null && sense.getBoxes().size() > 0) {
				faceInfoListRazor = new ArrayList<FaceInfo>(faceInfoList.size());
				for (Rect box : sense.getBoxes()) {
					if (box == null) {
						continue;
					}
					// if (faceInfoList.size() == 0) {
					// break;
					// }

					// ↓ 在图像上绘制boxes范围框
					int top = box.getTop() <= 0 ? 1 : box.getTop();
					int left = box.getLeft() <= 0 ? 1 : box.getLeft();
					int bottom = box.getBottom() >= image.getHeight() ? image.getHeight() - 1 : box.getBottom();
					int right = box.getRight() >= image.getWidth() ? image.getWidth() - 1 : box.getRight();
					for (int i = left; i < right; i++) {
						image.setRGB(i, top, 0xFF0000);
						image.setRGB(i, bottom, 0xFF0000);
					}
					for (int i = top; i < bottom; i++) {
						image.setRGB(left, i, 0xFF0000);
						image.setRGB(right, i, 0xFF0000);
					}
					// ↑ 在图像上绘制boxes范围框

					Iterator<FaceInfo> iter = faceInfoList.iterator();
					while (iter.hasNext()) {
						FaceInfo face = iter.next();
						// 有重叠
						boolean overlap = !(face.getRect().bottom < box.top || face.getRect().top > box.bottom
								|| face.getRect().left > box.right || face.getRect().right < box.left);
						// 全包含
						boolean inclusive = !(face.getRect().top < box.top || face.getRect().bottom > box.bottom
								|| face.getRect().right > box.right || face.getRect().left < box.left);
						if (sense.getInclusive() && inclusive || !sense.getInclusive() && overlap) {
							faceInfoListRazor.add(face);
							iter.remove();
						}
					}
				}
			} else {
				faceInfoListRazor = faceInfoList;
			}
			// ↑ 只保留在boxes范围内的的对象

			Map<String, Object> result = new HashMap<String, Object>(1);
			result.put("size", faceInfoListRazor.size());
			result.put("list", faceInfoListRazor);
			result.put("faceid", maxFaceId);

			result.put("age", faceService.getAge());
			result.put("gender", faceService.getGender());

			sense.setResult(result);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageIO.write(image, "JPG", output);
			sense.setBases64(Base64.encodeBase64String(output.toByteArray()));

			// BufferedImage image1 = ImageIO.read(new
			// ByteArrayInputStream(Base64.decodeBase64(sense.getBases64())));
			// ImageIO.write(image, "JPG", new File("image.jpg"));

			return sense;
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
		}
	}

}

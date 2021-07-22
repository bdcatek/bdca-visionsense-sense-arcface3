package com.bdca.sense.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.FunctionConfiguration;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.enums.DetectModel;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.arcsoft.face.toolkit.ImageInfoEx;
import com.bdca.sense.manager.FaceEngineManager;

@Service
public class FaceService {

	@Autowired
	private FaceEngineManager faceEngine;

	public List<FaceInfo> detectFaces(InputStream input) {
		// 人脸检测
		ImageInfo imageInfo = ImageFactory.getRGBData(input);
		return detectFaces(imageInfo);
	}

	public List<FaceInfo> detectFaces(ImageInfo imageInfo) {
		// 人脸检测
		List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
		ImageInfoEx imageInfoEx = new ImageInfoEx();
		imageInfoEx.setHeight(imageInfo.getHeight());
		imageInfoEx.setWidth(imageInfo.getWidth());
		imageInfoEx.setImageFormat(imageInfo.getImageFormat());
		imageInfoEx.setImageDataPlanes(new byte[][] { imageInfo.getImageData() });
		imageInfoEx.setImageStrides(new int[] { imageInfo.getWidth() * 3 });
		int errorCode = faceEngine.getFaceEngine().detectFaces(imageInfoEx, DetectModel.ASF_DETECT_MODEL_RGB,
				faceInfoList);

		// 功能配置
		FunctionConfiguration fun = new FunctionConfiguration();
		fun.setSupportAge(true);
		fun.setSupportGender(true);
		errorCode = faceEngine.getFaceEngine().process(imageInfoEx, faceInfoList, fun);

		return faceInfoList;
	}

	public List<AgeInfo> getAge() {
		// 人脸检测
		List<AgeInfo> ageInfoList = new ArrayList<AgeInfo>();
		int errorCode = faceEngine.getFaceEngine().getAge(ageInfoList);
		return ageInfoList;
	}

	public List<GenderInfo> getGender() {
		// 人脸检测
		List<GenderInfo> genderInfoList = new ArrayList<GenderInfo>();
		int errorCode = faceEngine.getFaceEngine().getGender(genderInfoList);
		return genderInfoList;
	}

	public List<FaceFeature> extractFaceFeature(InputStream input) {
		// 特征提取
		ImageInfo imageInfo = ImageFactory.getRGBData(input);
		List<FaceInfo> faceInfoList = detectFaces(imageInfo);
		List<FaceFeature> faceFeatureList = new ArrayList<FaceFeature>();
		for (FaceInfo faceInfo : faceInfoList) {
			FaceFeature faceFeature = new FaceFeature();
			int errorCode = faceEngine.getFaceEngine().extractFaceFeature(imageInfo.getImageData(),
					imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfo, faceFeature);
			faceFeatureList.add(faceFeature);
		}
		return faceFeatureList;
	}

	public float compareFaceFeature(FaceFeature feature1, FaceFeature feature2) {
		// 特征比对
		FaceFeature targetFaceFeature = new FaceFeature();
		targetFaceFeature.setFeatureData(feature1.getFeatureData());
		FaceFeature sourceFaceFeature = new FaceFeature();
		sourceFaceFeature.setFeatureData(feature2.getFeatureData());
		FaceSimilar faceSimilar = new FaceSimilar();

		for (byte b : feature1.getFeatureData()) {
			System.out.println(b);
		}
		System.out.println("---------");
		for (byte b : feature2.getFeatureData()) {
			System.out.println(b);
		}

		int errorCode = faceEngine.getFaceEngine().compareFaceFeature(targetFaceFeature, sourceFaceFeature,
				faceSimilar);

		return faceSimilar.getScore();
	}

}

package com.bdca.sense.manager;

import java.io.File;
import java.io.FileNotFoundException;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FunctionConfiguration;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;

@Service
@ConfigurationProperties(prefix = "arcface")
public class FaceEngineManager {

	// 从官网获取
	private String appId;
	private String sdkKey;
	private String libraryPath;
	private DetectMode detectMode;

	private FaceEngine faceEngine;

	private FaceEngine faceEngineImage;

	private FaceEngine faceEngineVideo;

	private int errorCode = -1;

	@PostConstruct
	private void initFaceEngine() throws Exception {

		// 获取跟目录
		File classPath = null;
		try {
			classPath = new File(ResourceUtils.getURL("classpath:").getPath());
		} catch (FileNotFoundException e) {
			// nothing to do
		}
		if (classPath == null || !classPath.exists()) {
			classPath = new File("");
		}

		// 激活引擎
		faceEngineImage = new FaceEngine(new File(libraryPath).getAbsolutePath());
		errorCode = faceEngineImage.activeOnline(appId, sdkKey);

		if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
			System.out.println("引擎激活失败: " + errorCode);
			// throw new IOException("引擎激活失败: " + errorCode);
			return;
		}

		ActiveFileInfo activeFileInfo = new ActiveFileInfo();
		errorCode = faceEngineImage.getActiveFileInfo(activeFileInfo);
		if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
			System.out.println("获取激活文件信息失败: " + errorCode);
			// throw new IOException("获取激活文件信息失败: " + errorCode);
			return;
		}

		// 激活引擎
		faceEngineVideo = new FaceEngine(new File(libraryPath).getAbsolutePath());
		errorCode = faceEngineVideo.activeOnline(appId, sdkKey);

		if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
			System.out.println("引擎激活失败: " + errorCode);
			// throw new IOException("引擎激活失败: " + errorCode);
			return;
		}

		activeFileInfo = new ActiveFileInfo();
		errorCode = faceEngineVideo.getActiveFileInfo(activeFileInfo);
		if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
			System.out.println("获取激活文件信息失败: " + errorCode);
			// throw new IOException("获取激活文件信息失败: " + errorCode);
			return;
		}

		// 引擎配置
		EngineConfiguration engineConfiguration = new EngineConfiguration();
		engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
		engineConfiguration.setDetectFaceMaxNum(10);
		// 功能配置
		FunctionConfiguration functionConfiguration = new FunctionConfiguration();
		functionConfiguration.setSupportAge(true);
		functionConfiguration.setSupportFace3dAngle(true);
		functionConfiguration.setSupportFaceDetect(true);
		functionConfiguration.setSupportFaceRecognition(true);
		functionConfiguration.setSupportGender(true);
		functionConfiguration.setSupportLiveness(true);
		functionConfiguration.setSupportIRLiveness(true);
		engineConfiguration.setFunctionConfiguration(functionConfiguration);

		// 初始化引擎
		engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
		errorCode = faceEngineImage.init(engineConfiguration);

		if (errorCode != ErrorInfo.MOK.getValue()) {
			System.out.println("初始化引擎失败: " + errorCode);
			// throw new IOException("初始化引擎失败: " + errorCode);
			return;
		}

		// 初始化引擎
		engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_VIDEO);
		errorCode = faceEngineVideo.init(engineConfiguration);

		if (errorCode != ErrorInfo.MOK.getValue()) {
			System.out.println("初始化引擎失败: " + errorCode);
			// throw new IOException("初始化引擎失败: " + errorCode);
			return;
		}
	}

	public FaceEngine getFaceEngine() {
		return faceEngine == null ? faceEngineImage : faceEngine;
	}

	public FaceEngine setFaceEngine(FaceEngine engine) {
		return faceEngine = engine;
	}

	public FaceEngine getFaceEngineImage() {
		return faceEngineImage;
	}

	public FaceEngine getFaceEngineVideo() {
		return faceEngineVideo;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getSdkKey() {
		return sdkKey;
	}

	public void setSdkKey(String sdkKey) {
		this.sdkKey = sdkKey;
	}

	public String getLibraryPath() {
		return libraryPath;
	}

	public void setLibraryPath(String libraryPath) {
		this.libraryPath = libraryPath;
	}

	public DetectMode getDetectMode() {
		return detectMode;
	}

	public void setDetectMode(DetectMode detectMode) {
		this.detectMode = detectMode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}

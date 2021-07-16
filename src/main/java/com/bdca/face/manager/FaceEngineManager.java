package com.bdca.face.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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

	private FaceEngine faceEngine;

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

		faceEngine = new FaceEngine(new File(libraryPath).getAbsolutePath());
		// 激活引擎
		errorCode = faceEngine.activeOnline(appId, sdkKey);

		if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
			System.out.println("引擎激活失败: " + errorCode);
			// throw new IOException("引擎激活失败: " + errorCode);
			return;
		}

		ActiveFileInfo activeFileInfo = new ActiveFileInfo();
		errorCode = faceEngine.getActiveFileInfo(activeFileInfo);
		if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
			System.out.println("获取激活文件信息失败: " + errorCode);
			// throw new IOException("获取激活文件信息失败: " + errorCode);
			return;
		}

		// 引擎配置
		EngineConfiguration engineConfiguration = new EngineConfiguration();
		engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
		engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
		engineConfiguration.setDetectFaceMaxNum(10);
		engineConfiguration.setDetectFaceScaleVal(16);
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
		errorCode = faceEngine.init(engineConfiguration);

		if (errorCode != ErrorInfo.MOK.getValue()) {
			System.out.println("初始化引擎失败: " + errorCode);
			// throw new IOException("初始化引擎失败: " + errorCode);
			return;
		}
	}

	public FaceEngine getFaceEngine() {
		return faceEngine;
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

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}

package com.bdca.face.service;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.toolkit.ImageInfo;
import com.bdca.face.manager.FaceEngineManager;

@Service
public class FaceService {

	@Autowired
	private FaceEngineManager faceEngine;

	public List<FaceInfo> detectFaces(InputStream input) {
		// 人脸检测
		ImageInfo imageInfo = getRGBData(input);
		List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
		int errorCode = faceEngine.getFaceEngine().detectFaces(imageInfo.getImageData(), imageInfo.getWidth(),
				imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
		return faceInfoList;
	}

	public List<FaceInfo> detectFaces(ImageInfo imageInfo) {
		// 人脸检测
		List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
		int errorCode = faceEngine.getFaceEngine().detectFaces(imageInfo.getImageData(), imageInfo.getWidth(),
				imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
		return faceInfoList;
	}

	public List<FaceFeature> extractFaceFeature(InputStream input) {
		// 特征提取
		ImageInfo imageInfo = getRGBData(input);
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

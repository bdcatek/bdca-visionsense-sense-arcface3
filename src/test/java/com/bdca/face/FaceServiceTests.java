package com.bdca.face;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.bdca.face.service.FaceService;
import com.sun.jna.Platform;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FaceServiceTests {

	@Autowired
	FaceService faceService;

	@Test
	public void comparison() {
		// faceService.comparison("1-2.jpg", "1-2.jpg");
	}

	public static void main(String args[]) throws IOException, InterruptedException {
		// for (int i = 0; i < 100; i++) {
		// System.out.printf("%d\t%f\n", i, 1 / (1 + Math.pow(Math.E, -i)));
		// }
		System.out.println(FaceServiceTests.readSystemStartTime());
	}

	public static String readSystemStartTime() throws IOException, InterruptedException {
		if (Platform.isWindows()) {
			Process process = Runtime.getRuntime().exec("cmd /c net statistics workstation");
			String startUpTime = "";
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			int i = 0;
			String timeWith = "";
			while ((timeWith = bufferedReader.readLine()) != null) {
				if (i == 3) {
					System.out.println(timeWith);
					startUpTime = timeWith;
				}
				i++;
			}
			process.waitFor();
			return startUpTime;
		} else {
			String[] cmd = { "cat", "/proc/uptime" };
			Process process = Runtime.getRuntime().exec(cmd);
			// .exec("/bin/date -d \"$(awk -F. '{print $1}' /proc/uptime) second ago\"
			// +\"%Y-%m-%d %H:%M:%S\"");
			// //取得命令结果的输出流
			InputStream fis = process.getInputStream();
			// 用一个读输出流类去读
			InputStreamReader isr = new InputStreamReader(fis);
			// 用缓冲器读行
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			// 直到读完为止
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				break;
			}
			process.waitFor();
			long now = System.currentTimeMillis();
			long key = (now / 1000 - Double.valueOf(line.split(" ")[0]).longValue()) / 60;
			// System.out.println(now - Double.valueOf(line.split(" ")[1]).longValue() *
			// 1000);
			System.out.println(key);
			Random r = new Random(key);
			System.out.println(r.nextFloat());
			System.out.println(r.nextInt());
			System.out.println(r.nextLong());
			return line;
		}
	}
}

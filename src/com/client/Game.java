package com.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Game {

	int cannum = 27;

	
	File quizs;
	FileReader rdtx;
	FileWriter wrtx;
	StringTokenizer k;
	String[] words = new String[cannum];
	String line = new String();
	
	public Game() {
		quizs = new File("quiz01.txt");
		if (quizs.exists()) {
			System.out.println("퀴즈 파일 존재");
		} else {
			try {
				System.out.println("파일 생성");
				quizs.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			rdtx = new FileReader(quizs);
			wrtx = new FileWriter(quizs,true);

			if (rdtx.read() == -1) {				
				line = "축구 철새 개구리 호두나무 모자 기둥 요구르트 쌀국수 늑대 김민엽 얼룩말 마녀 황소 악마 짱구 세일러문 건담 이름 인터스텔라 초시계 건포도 레옹 미녀 야수 담배 공부 인셉션 수박 눈알 타이타닉 겨울왕국 명량 에일리언";
				
				wrtx.write(line);
				words = line.split(" ");
				System.out.println("입력되어 들어간 단어들 : "+ Arrays.toString(words));
				wrtx.close();

			} else {
				FileReader fr = new FileReader(quizs);
				BufferedReader qzl = new BufferedReader(new FileReader(quizs));

				words = qzl.readLine().split(" ");
				
				System.out.println("이미 있는 단어들 : "+ Arrays.toString(words));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String ExtractQuiz() throws IOException {
		return this.words[(int) ((Math.random() * (cannum-1)))];
	}

}

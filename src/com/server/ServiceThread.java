package com.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

public class ServiceThread extends Thread {

	private ChatServer server;
	private Socket socket;
	private int userScore, score;
	private boolean auth;
	private StringTokenizer str;
	private String userName, message, protocol, msg, tempans;
	private PrintWriter out;
	private BufferedReader in;

	private Vector<String> outName = new Vector<String>(); ///
	private int outNum; ///

	switchRandomAuth sra;

	public ServiceThread(ChatServer server, Socket socket, boolean auth) {
		this.server = server;
		this.socket = socket;
		this.auth = auth;
	}

	public void sendMessage(String msg) throws IOException {
		if (out != null) {
			out.println(msg);
		}
	}

	public String getUserName() {
		return userName;
	}

	public int getOutNum() { ///
		return outNum;
	}

	public void setOutNum(int outNum) { ///
		this.outNum = outNum;
	}

	public void setScore(int score) {
		this.userScore = score;
	}

	public int getScore() {
		return userScore;
	}
	
	public boolean getUserAuth() {
		return auth;
	}
	public void setUserAuth(boolean auth) {
		this.auth = auth;
	}

//	public String toString() {
//		return userName;
//	}

	public void run() {
		try {
			System.out.println("클라이언트 \n" + socket + "\n에서 접속하였습니다.");

			// 메세지 입출력 객체를 소켓에서 받아온다.
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// 줄바꿈

			String tmpName;

			while (true) {
				tmpName = in.readLine();
				if (server.checkUserName(tmpName) == false) {
					userName = tmpName;
					break;
				} else {
					sendMessage("err/아이디중복");
				}
			}

			server.sendMessageAll("NewUser/" + "알림 : *******" + userName + "님이 입장하셨습니다******");
			server.setUserList();
			server.setRankList();
			server.firstAuthUser();
			score = 0;
			while ((message = in.readLine()) != null) {
				inMessage(message);
			}
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			try {
				server.removeClient(this);
				server.setUserList();
				
				
				if(auth) {
					server.randomAuthUser();
				}
				server.sendMessageAll("Chatting/[" + userName + "] 님이 나가셨습니다.");

			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("클라이언트 " + socket + " 에서 접속이 끊겼습니다...");
		}
	}

	public void inMessage(String message) throws IOException {
		str = new StringTokenizer(message, "/");

		while (str.hasMoreTokens()) { ///
			protocol = (String) str.nextElement();
			msg = str.nextToken();

			switch (protocol) {
			case "Chatting":
				server.sendMessageAll("Chatting/" + " [ " + userName + " ] : " + msg);

				if (msg.equals(server.answer)) {
					tempans = server.answer;
					server.answer = "누구보다빠르게난남들과는다르게";
					server.sendMessageAll("Chatting/" + userName + "님이 정답 " + tempans + "를(을) 맞췄습니다!");
					server.sendMessageAll("Yougotit/" + userName + "님이 정답 " + tempans + "를(을) 맞췄습니다.");
					userScore++;
					server.setRankList();
					cochau();

					break;
				}

				if (msg.equals("score"))
					sendMessage("Chatting/" + " [ " + userName + " ] 님의 점수는 " + Integer.toString(userScore) + "점 입니다.");
				break;

			case "DragMouse":
				server.sendMessageAll("DragMouse/" + msg);
//			sendMessage("DragMouse/");
				break;

			case "Timer":
				server.answer = server.game.ExtractQuiz();
				sra = new switchRandomAuth();
				sra.start();

				server.sendMessageAll("Timer/" + msg);
				sendMessage("RightAnswer/" + server.answer);
				break;

			case "Erase":
				server.sendMessageAll("Erase/지우기");
				break;
			case "UserOut":
				if (outName.indexOf(msg) == -1 && !msg.equals(userName)) {
					for (int i = 0; i < server.Clients.size(); i++) {
						if (msg.equals(server.Clients.elementAt(i).userName)) {
							outName.add(server.Clients.elementAt(i).userName);
							sendMessage("Chatting/당신은" + msg + "님에게 강퇴표를 날렸습니다 ㅋ");

							server.userOut(i);
						}
					}
				} else if (msg.equals(userName)) {
					sendMessage("Chatting/* 자신을 강퇴 할 수 없습니다. ");
				} else if (outName.indexOf(msg) != -1)
					sendMessage("Chatting/" + msg + "님에 대한 강퇴표를 이미 사용했습니다. ");
				break;
				
			case "Whisper":

				str = new StringTokenizer(msg, "@");
				while (str.hasMoreTokens()) {
					String toUser = str.nextToken();
					String note = str.nextToken();
					server.whisper(toUser, userName, note);
				}
				break;
				
			case "Emoticon":
				server.sendMessageAll("Emoticon/이모티콘출력");
			}
		}
	}

	public void cochau() {
		for (int i = 0; i < server.Clients.size(); i++) {
			if (server.Clients.get(i).auth == true) {
				server.Clients.get(i).sra.killa();
			}
		}
	}

	public void close() throws IOException {///

		out.close();
		in.close();
		socket.close();

	}///

	public class switchRandomAuth extends Thread {
		int k = 0;

		public void killa() {
			k = 1011;
		}

		@Override
		public void run() {
			try {
				while (k < 940) {
					Thread.sleep(100);
					k++;
				}
				server.randomAuthUser();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

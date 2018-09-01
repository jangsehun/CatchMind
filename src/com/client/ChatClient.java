package com.client;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import com.drawing.SwingPaint;

public class ChatClient extends Frame implements ActionListener, MouseMotionListener, MouseListener {

	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	String ip, id, protocol, message, emoticon;
	String[] arr;
	StringTokenizer str;

	List user_List, rank_List;
	TextArea outputArea;
	TextField inputField, ip_tf, id_tf;
	JButton login_Btn, out_Btn, whisper_Btn, emoticon_Btn;
	JPanel South_Panel, East_Panel, Northeast_Panel, panel04;
	Label id_Label, ip_Label;
	
	// drawing panel variables
	int oldX, oldY, currentX, currentY;
	int oldX_screen, oldY_screen, currentX_screen, currentY_screen;
	int red, green, blue;
	int delaytime;
	SwingPaint swing;
	String[] color;
	Color Mixed_Color;
	int thick, thick_screen;
	boolean flag = true;

	public static void main(String[] args) {
		ChatClient client = new ChatClient();
		client.Connection();
		
	}

	public ChatClient() {
		GUI();
		Action();
	}

	public void Action() {
		inputField.addActionListener(this);
		login_Btn.addActionListener(this);
		swing.drawArea.addMouseListener(this);
		swing.drawArea.addMouseMotionListener(this);
		swing.startBtn.addActionListener(this);
		swing.clearBtn.addActionListener(this);
		out_Btn.addActionListener(this);
		whisper_Btn.addActionListener(this);
		emoticon_Btn.addActionListener(this);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		currentX = e.getX();
		currentY = e.getY();

		if (swing.drawArea.g2 != null && flag && !id.equals("")) {
			color = swing.drawArea.g2.getPaint().toString().split("=");
			System.out.println(swing.drawArea.g2.getPaint());
			for (int i = 0; i < 3; i++)
				color[i] = color[i].replaceAll("[^0-9]", "");
			color[3] = color[3].replaceAll("[^0-9]", "");

			thick = swing.drawArea.thickness;
			send_message("DragMouse/" + oldX + ":" + oldY + ":" + currentX + ":" + currentY + ":" + color[1] + ":"
					+ color[2] + ":" + color[3] + ":" + thick);
			swing.drawArea.g2.drawLine(oldX, oldY, currentX, currentY);
			swing.drawArea.repaint();
			oldX = currentX;
			oldY = currentY;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		oldX = e.getX();
		oldY = e.getY();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == login_Btn) {
			id = id_tf.getText().trim();
			if(!id.equals("")) {
				send_message(id); //닉네임을 체크하기 위해 버튼이 눌리면 입력한 닉네임이 서버로 전송된다
				System.out.println("서버에 접속했습니다.");
				id_tf.setEnabled(false);
				login_Btn.setEnabled(false);
				id_Label.setEnabled(false);
				inputField.setEnabled(true);
			}
		}

		else if (e.getSource() == inputField) {
			String input = inputField.getText().trim();
			inputField.setText("");
			try {
				if(!input.equals("")){
					send_message("Chatting/" + input);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		else if (e.getSource() == swing.startBtn) {
			if(!id.equals("")) {
				swing.drawArea.clear();
				send_message("Timer/" + 1000);
				swing.startBtn.setEnabled(false);
			}
		}

		else if (e.getSource() == swing.clearBtn) {
			if(!id.equals("")) {
				send_message("Erase/지우기");
			}
		}
		
		else if (e.getSource() == emoticon_Btn) {
			if(!id.equals("")) {
				System.out.println("clicked!");
				send_message("Emoticon/이모티콘보내기");
			}
		}
		
		else if (e.getSource() == out_Btn) {///
			try {
				if(!id.equals("")) {
					send_message("UserOut/" + user_List.getSelectedItem());
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == whisper_Btn) {///
			try {
				if(!id.equals("")) {
					String toUser = (String) user_List.getSelectedItem();
					String note = JOptionPane.showInputDialog("Send message");
					if (note != null) {
					send_message("Whisper/" + toUser.trim() + "@" + note);
					}
					System.out.println("Person to receive : " + toUser + " | contents : " + note);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		///
	}
	//192.168.10.33
	public void Connection() {
		try {
			clientSocket = new Socket("127.0.0.1", 9999); // 서버 접속
			System.out.println("서버 접속완료");
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			start();
		} catch (Exception e) {
			System.err.println("입출력 에러입니다.");
			System.exit(1); // 에러일 때 종료 -> 그냥 종료 : System.exit(0);
		}
	}

	public void start() {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						inMessage(in.readLine() + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		th.start();
	}

	public void Disconnection() {
		try {
			in.close();
			out.close();
			clientSocket.close();
		} catch (IOException e) {
		}
	}

	public void send_message(String str) {
		out.println(str);
	}

	public void inMessage(String msg) {
		str = new StringTokenizer(msg, "/");

		while (str.hasMoreTokens()) { ///
			protocol = (String) str.nextElement();
			message = str.nextToken();

			switch (protocol) {
			case "Chatting":
				outputArea.append(message);
				break;

			case "NewUser":
				outputArea.append(message);
				break;

			case "DragMouse":

				arr = message.split(":");
				oldX_screen = Integer.parseInt(arr[0]);
				oldY_screen = Integer.parseInt(arr[1]);
				currentX_screen = Integer.parseInt(arr[2]);
				currentY_screen = Integer.parseInt(arr[3]);
				red = Integer.parseInt(arr[4]);
				green = Integer.parseInt(arr[5]);
				blue = Integer.parseInt(arr[6]);
				arr[7] = arr[7].replaceAll("[^0-9]", "");
				thick_screen = Integer.parseInt(arr[7]);

				Mixed_Color = new Color(red, green, blue);

				swing.drawArea.g2.setPaint(Mixed_Color);
				swing.drawArea.g2.setStroke(new BasicStroke(thick_screen));
				swing.drawArea.g2.drawLine(oldX_screen, oldY_screen, currentX_screen, currentY_screen);
				swing.drawArea.repaint();
				break;

			case "Erase":
				swing.drawArea.clear();
				break;

			case "SetUserList":
				user_List.add(message + "\n");
				break;

			case "CleanUserList":
				user_List.removeAll();
				break;

			case "ChatClear":
				outputArea.setText("");
				outputArea.append(message);
				break;

			case "CleanRankList":
				rank_List.removeAll();
				break;

			case "SetRankList":
				rank_List.add(message);
				break;

			case "unAuth":
				isAuth(false);
				break;

			case "Auth":
				isAuth(true);
				break;

			case "Timer":
				msg = msg.replaceAll("[^0-9]", "");
				delaytime = Integer.parseInt(msg);
				swing.resetTimer();
				swing.startTimer(delaytime);
				swing.drawArea.clear();
				break;

			case "err":
				JOptionPane.showMessageDialog(null, "다른 아이디를 입력해주세요.", "경고", JOptionPane.ERROR_MESSAGE);
				id_tf.setEnabled(true);
				login_Btn.setEnabled(true);
				id_Label.setEnabled(true);
				inputField.setEnabled(false);
				break;

			case "RightAnswer":
				JOptionPane.showMessageDialog(null, "그림을 그리세요. 정답은 " + message + "입니다!", "출제자 당첨!",JOptionPane.INFORMATION_MESSAGE);
				inputField.setEnabled(false);
				break;

			case "Yougotit":
				swing.worktask.cancel();
				JOptionPane.showMessageDialog(null, message, "정답 발견!", JOptionPane.INFORMATION_MESSAGE);

				break;
			case "YouAreOut": ///
				System.exit(0);
				break;
				
			case "Whisper":
				str = new StringTokenizer(message,"@");	
				String fromUser = str.nextToken();
				String note = str.nextToken();
				
				System.out.println(fromUser + "note from the Client " + note);
				
				JOptionPane.showMessageDialog(null, note, "Message from " + fromUser, JOptionPane.CLOSED_OPTION);
				break;
				
			case "Emoticon":
				emoticon = "⊂_ヽ\r\n" + 
							"　 ＼＼ Λ＿Λ\r\n" + 
							"　　 ＼( ‘ㅅ' ) 두둠칫\r\n" + 
							"　　　 >　⌒ヽ\r\n" + 
							"　　　/ 　 へ＼\r\n" + 
							"　　 /　　/　＼＼\r\n" + 
							"　　 ﾚ　ノ　　 ヽ_つ\r\n" + 
							"　　/　/두둠칫\r\n" + 
							"　 /　/|\r\n" + 
							"　(　(ヽ\r\n" + 
							"　|　|、＼\r\n" + 
							"　| 丿 ＼ ⌒)\r\n" + 
							"　| |　　) /\r\n" + 
							"`ノ )　　Lﾉ\r\n";
				System.out.println("이모티콘 : " + emoticon);
				outputArea.append(emoticon);
			}
		}
	}

	private void isAuth(boolean isAuth) {
		swing.startBtn.setEnabled(isAuth);
		swing.blackBtn.setEnabled(isAuth);
		swing.blueBtn.setEnabled(isAuth);
		swing.clearBtn.setEnabled(isAuth);
		swing.eraseBtn.setEnabled(isAuth);
		swing.greenBtn.setEnabled(isAuth);
		swing.magentaBtn.setEnabled(isAuth);
		swing.redBtn.setEnabled(isAuth);
		swing.thickMinusBtn.setEnabled(isAuth);
		swing.thickPlusBtn.setEnabled(isAuth);
		swing.Rscr.setEnabled(isAuth);
		swing.Gscr.setEnabled(isAuth);
		swing.Bscr.setEnabled(isAuth);
		inputField.setEnabled(!isAuth);
		flag = isAuth;
	}

	private void GUI() {
		setLayout(new BorderLayout());
		class CustomCellRenderer implements ListCellRenderer {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				Component component = (Component) value;
				return component;
			}
		}

		swing = new SwingPaint();
		outputArea = new TextArea();
		outputArea.setEditable(false);
		user_List = new List();
		rank_List = new List();
		inputField = new TextField();
		inputField.setColumns(40);
		inputField.setEnabled(false);

		id_Label = new Label("ID :");
		id_tf = new TextField();
		id_tf.setColumns(20);
		login_Btn = new JButton("Login");
		out_Btn = new JButton("out");
		whisper_Btn = new JButton("message");
		emoticon_Btn = new JButton("emoji");

		emoticon_Btn.setBorderPainted(false);
		emoticon_Btn.setFocusPainted(false);
		emoticon_Btn.setBackground(Color.WHITE);
		login_Btn.setBorderPainted(false);
		login_Btn.setFocusPainted(false);
		login_Btn.setBackground(Color.WHITE);
		out_Btn.setBorderPainted(true);///
		out_Btn.setFocusPainted(false);
		out_Btn.setBackground(Color.WHITE);
		whisper_Btn.setBorderPainted(true);///
		whisper_Btn.setFocusPainted(false);///
		whisper_Btn.setBackground(Color.WHITE);///

		South_Panel = new JPanel();
		East_Panel = new JPanel();
		Northeast_Panel = new JPanel();
		panel04 = new JPanel();
		
		swing.function.add(rank_List);

		South_Panel.setLayout(new FlowLayout());
		East_Panel.setLayout(new BorderLayout());
		Northeast_Panel.setLayout(new BorderLayout());
		panel04.setLayout(new BorderLayout());

		// outButtonsize
		Panel out = new Panel();
		GridBagLayout g = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.HORIZONTAL;

//		c.fill = GridBagConstraints.NONE;
		out.setLayout(g);
		out.add(out_Btn, c);
		out.add(emoticon_Btn,c);
		out.add(whisper_Btn,c);
		
		
		// -------------------

		South_Panel.add(id_Label);
		South_Panel.add(id_tf);
		South_Panel.add(login_Btn);

		Northeast_Panel.add(user_List, BorderLayout.CENTER);
		Northeast_Panel.add(out, BorderLayout.EAST);

		panel04.add(outputArea, BorderLayout.CENTER);
		panel04.add(inputField, BorderLayout.SOUTH);

		East_Panel.add(Northeast_Panel, BorderLayout.NORTH);
		East_Panel.add(panel04, BorderLayout.CENTER);

		add(South_Panel, BorderLayout.SOUTH);
		add(East_Panel, BorderLayout.EAST);
		add(swing.content, BorderLayout.CENTER);

		South_Panel.setBackground(new Color(42, 153, 211));
		East_Panel.setBackground(new Color(42, 153, 211));
		Northeast_Panel.setBackground(new Color(42, 153, 211));

		Northeast_Panel.setBorder(new EmptyBorder(10, 0, 10, 10));
		East_Panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		pack();
		setSize(1400, 800);
		setVisible(true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

}

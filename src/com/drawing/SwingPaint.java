package com.drawing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

public class SwingPaint extends JFrame {
	
	public JButton clearBtn, blackBtn, blueBtn, greenBtn, redBtn, magentaBtn, thickPlusBtn, thickMinusBtn, eraseBtn, startBtn;
	public DrawArea drawArea;
	public JPanel container, controls, scroll, function, paint;
	public Scrollbar Rscr,Bscr,Gscr;
	public Container content;
	public JLabel clk_label, west; 
	public JProgressBar progress_bar;
	public int min = 1;
	public int sec = 30;
	public int mili = 0;
	
	public Timer timer;
	public WorkTask worktask;
	
	ActionListener actionListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource() == clearBtn) {
				drawArea.clear();
			}
			else if (e.getSource() == blackBtn) {
				drawArea.black();
			}
			else if (e.getSource() == blueBtn) {
				drawArea.blue();
			}
			else if (e.getSource() == greenBtn) {
				drawArea.green();
			}
			else if (e.getSource() == redBtn) {
				drawArea.red();
			}
			else if (e.getSource() == magentaBtn) {
				drawArea.magenta();
			}
			else if (e.getSource() == thickPlusBtn) {
				drawArea.thickPlus();
			}
			else if (e.getSource() == thickMinusBtn) {
				drawArea.thickMinus();
			}
			else if (e.getSource() == eraseBtn) {
				drawArea.white();
			}
		}
	};

	public static void main(String[] args) {
		new SwingPaint();
	}
	
	public SwingPaint() {
		//https://undocumentedmatlab.com/blog/sliders-in-matlab-gui	slider
		//JFrame frame = new JFrame("Swing Paint");
		content = this.getContentPane();
		content.setLayout(new BorderLayout());
		content.setFont(new Font("Consolas", Font.PLAIN, 15));
		drawArea = new DrawArea();

		//create controls to apply colors and call clear feature
		controls = new JPanel();

		clearBtn = new JButton("Clear");
		clearBtn.addActionListener(actionListener);
		blackBtn = new JButton("Black");
		blackBtn.addActionListener(actionListener);
		blueBtn = new JButton("Blue");
		blueBtn.addActionListener(actionListener);
		greenBtn = new JButton("Green");
		greenBtn.addActionListener(actionListener);
		redBtn = new JButton("Red");
		redBtn.addActionListener(actionListener);
		magentaBtn = new JButton("Magenta");
		magentaBtn.addActionListener(actionListener);
		clk_label = new JLabel(min + " : " + sec + " : " + mili);
		
		
		controls.add(greenBtn);
		controls.add(blueBtn);
		controls.add(blackBtn);
		controls.add(redBtn);
		controls.add(magentaBtn);
		controls.add(clearBtn);
		controls.add(clk_label);
		controls.setBackground(new Color(42,153,211));
		
		//create color scroll
		scroll = new JPanel(new GridLayout(3,1));
		
		Rscr = new Scrollbar(Scrollbar.HORIZONTAL, 255, 1, 0, 255);
		Bscr = new Scrollbar(Scrollbar.HORIZONTAL, 255, 1, 0, 255);
		Gscr = new Scrollbar(Scrollbar.HORIZONTAL, 255, 1, 0, 255);
		
		scroll.setSize(100,100);
		scroll.add(Rscr);
		scroll.add(Bscr);
		scroll.add(Gscr);
		
		Rscr.setBackground(new Color(225,50,60));
		Bscr.setBackground(new Color(38,75,150));
		Gscr.setBackground(new Color(5,153,50));
		
		Rscr.addAdjustmentListener(new SliderEvent() {});
		Bscr.addAdjustmentListener(new SliderEvent() {});
		Gscr.addAdjustmentListener(new SliderEvent() {});
		
		//function
		GridLayout grid = new GridLayout(8,1);
		grid.setHgap(10);
		grid.setVgap(10);
		function = new JPanel(grid);
		function.setBorder(new EmptyBorder(10, 10, 10, 10));
		function.setBackground(new Color(42,153,211));
		thickPlusBtn = new JButton("+");
		thickPlusBtn.addActionListener(actionListener);
		thickMinusBtn = new JButton("-");
		thickMinusBtn.addActionListener(actionListener);
		eraseBtn = new JButton("Erase");
		eraseBtn.addActionListener(actionListener);
		timer = new Timer();
		startBtn = new JButton("START");
		
		thickPlusBtn.setPreferredSize(new Dimension(5, 5));
		

		function.add(thickPlusBtn);
		function.add(thickMinusBtn);
		function.add(eraseBtn);
		function.add(startBtn);
		
		paint = new JPanel(new BorderLayout());
		paint.add(drawArea, BorderLayout.CENTER);
		paint.add(scroll, BorderLayout.SOUTH);
		paint.setBorder(new EmptyBorder(10, 20, 5, 0));
		paint.setBackground(new Color(42,153,211));
		
		
		content.add(paint, BorderLayout.CENTER);
		content.add(controls, BorderLayout.NORTH);
		content.add(function,BorderLayout.EAST);

//			this.setSize(600, 600);
		//can close frame
//			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//show the swing paint result
//				this.setVisible(true);
		//Now you can try our Swing Paint !! Enjoy :D
		
//			clearBtn.setContentAreaFilled(false);
		clearBtn.setBorderPainted(false);
		clearBtn.setFocusPainted(false);
		clearBtn.setBackground(Color.WHITE);
		blackBtn.setBorderPainted(false);
		blackBtn.setFocusPainted(false);
		blackBtn.setBackground(Color.WHITE);
		redBtn.setBorderPainted(false);
		redBtn.setFocusPainted(false);
		redBtn.setBackground(Color.WHITE);
		blueBtn.setBorderPainted(false);
		blueBtn.setFocusPainted(false);
		blueBtn.setBackground(Color.WHITE);
		magentaBtn.setBorderPainted(false);
		magentaBtn.setFocusPainted(false);
		magentaBtn.setBackground(Color.WHITE);
		greenBtn.setBorderPainted(false);
		greenBtn.setFocusPainted(false);
		greenBtn.setBackground(Color.WHITE);
		thickPlusBtn.setBorderPainted(false);
		thickPlusBtn.setFocusPainted(false);
		thickPlusBtn.setBackground(Color.WHITE);
		thickMinusBtn.setBorderPainted(false);
		thickMinusBtn.setFocusPainted(false);
		thickMinusBtn.setBackground(Color.WHITE);
		eraseBtn.setBorderPainted(false);
		eraseBtn.setFocusPainted(false);
		eraseBtn.setBackground(Color.WHITE);
		startBtn.setBorderPainted(false);
		startBtn.setFocusPainted(false);
		startBtn.setBackground(Color.WHITE);
	  

	}
	
	
	
	class SliderEvent implements AdjustmentListener{

		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {
			// TODO Auto-generated method stub
			int red = 0;
			int blue = 0;
			int green = 0;
			
			red = Rscr.getValue();
			blue = Gscr.getValue();
			green = Bscr.getValue();
			
			drawArea.mixed(red, blue, green);
		}
	}
	
	public void resetTimer() {
		min = 1;
		sec = 30;
		mili = 5;
		clk_label.setText(min+ " : " + sec + " : " + mili);
	}
	
	public void startTimer(int delay) {
		worktask = new WorkTask();
		timer.schedule(worktask, delay, 10);
	}
	
	public class WorkTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			clk_label.setText(min+ " : " + sec + " : " + mili);
			
			if(mili == 0) {
				if(sec == 0) {
					if (min >= 1) {
						sec = 59;
						min--;
					}
					else {
						cancel();
						min = 1;
						sec = 30;
						clk_label.setText(min+ " : " + sec + " : " + mili);
					}
				}
				else {
					mili = 99;
					sec--;
				}
			}
			else mili--;
		}
		
	}
}

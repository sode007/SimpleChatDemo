package com.han;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

public class TalkFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MainFrame mainframe = null;
	private Socket socket = null;
	private BufferedReader br = null;
	private PrintWriter pw = null;
	ReciveThread recivethread = null;
	// 发送按钮的事件处理
	private ActionListener sendhandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			String inputContent = infofield2.getText().trim();
			if (inputContent.length() > 0) {
				infofield.append("我" + inputContent + "\n");
				pw.println(inputContent);
				pw.flush();
				infofield2.setText("");
			}
		}
	};
	private WindowListener windowlistener = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {

			recivethread.stopThread();
			if (pw != null) {
				pw.close();
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e1) {
					// e1.printStackTrace();
				}
			}

			try {
				socket.close();
			} catch (IOException e1) {
				// e1.printStackTrace();
			}
			TalkFrame.this.dispose();
			mainframe.setVisible(true);
		};
	};

	private JLabel infolabel = null;
	private JTextArea infofield = null;
	private JLabel infolabel2 = null;
	private JTextArea infofield2 = null;
	private JButton sendbutton = null;

	public TalkFrame(MainFrame mainframe, Socket socket) {
		super("聊天窗口");
		this.mainframe = mainframe;
		this.socket = socket;

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		this.addWindowListener(windowlistener);

		inticomponent();

		JPanel pnl = getPnanel();
		this.add(pnl);

		pack();
		// 根據socket初始化流
		try {
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "utf-8"));
			pw = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream(), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 启动监听线程
		recivethread = new ReciveThread();
		recivethread.start();

	}

	/**
	 * 进行界面布局
	 * 
	 * @return
	 */
	private JPanel getPnanel() {

		Border border = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		JPanel pnl = new JPanel();
		pnl.setLayout(new GridBagLayout());

		GridBagConstraints cons = Utils.getGridBagconstraints(0, 0, 1, 1, 100,
				100, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		cons.insets = new Insets(5, 0, 5, 0);
		infolabel.setBorder(border);
		pnl.add(infolabel, cons);

		cons = Utils.getGridBagconstraints(0, 1, 1, 1, 100, 800,
				GridBagConstraints.BOTH, GridBagConstraints.WEST);
		cons.insets = new Insets(5, 5, 5, 5);
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(infofield);
		pnl.add(sp, cons);

		cons = Utils.getGridBagconstraints(0, 3, 1, 1, 100, 100,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		cons.insets = new Insets(5, 0, 5, 0);
		infolabel2.setBorder(border);
		pnl.add(infolabel2, cons);

		cons = Utils.getGridBagconstraints(0, 4, 1, 1, 100, 300,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		cons.insets = new Insets(5, 5, 5, 5);
		JScrollPane sp2 = new JScrollPane();
		sp2.setViewportView(infofield2);
		pnl.add(sp2, cons);

		cons = Utils.getGridBagconstraints(0, 5, 1, 1, 100, 100,
				GridBagConstraints.NONE, GridBagConstraints.EAST);
		cons.insets = new Insets(5, 0, 5, 5);
		pnl.add(sendbutton, cons);
		return pnl;
	}

	private void inticomponent() {
		infolabel = new JLabel();
		
		
		StringBuffer sb = new StringBuffer("对方的ip：");
		if (this.socket != null) {
			sb.append(socket.getInetAddress().getHostAddress());
			sb.append("对方的端口： ");
			sb.append(String.valueOf(socket.getPort()));
		}
		;

		infolabel.setText(sb.toString());
		infofield = new JTextArea(25, 35);
		infolabel2 = new JLabel("请输入你要说的话，然后单击【发送】按钮： ");
		infofield2 = new JTextArea(5, 35);
		sendbutton = new JButton("发送");
		sendbutton.addActionListener(sendhandler);
	}

	// 监听线程类
	private class ReciveThread extends Thread {
		private boolean running = true;

		public void stopThread() {
			running = false;
		}

		@Override
		public void run() {
			try {
				while (running) {
					String temp = br.readLine();
					if (temp != null) {
						infofield.append("对方：" + temp + "\n");
					}
				}
			} catch (IOException e) {
				// e.printStackTrace();
			}

		}
	}

}

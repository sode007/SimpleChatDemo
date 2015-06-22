package com.han;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MainFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel targetip = null;
	private JTextField targetipfield = null;
	private JLabel targetport = null;
	private JTextField targetportfield = null;
	private JButton button = null;
	private static final int LINSTENER_PORT = 2995;
	private ServerSocket serversocket = null;
	private ServerListenerThread listenerthread = null;
	private ActionListener starthandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (targetipfield.isValid()) {
				try {
					InetAddress TargetIp = InetAddress.getByName(targetipfield
							.getText());
					int TargetPot = Integer.parseInt(targetportfield.getText());

					try {
						Socket soc = new Socket(TargetIp, TargetPot);
						// TODO 显示聊天窗口
						TalkFrame talkframe = new TalkFrame(MainFrame.this, soc);
						MainFrame.this.setVisible(false);
						talkframe.setVisible(true);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (UnknownHostException e2) {
					e2.printStackTrace();
				}
			}
		}
	};
	// 添加窗口监听器
	private WindowListener windowlistener = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			listenerthread.stopThread();
			try {
				serversocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			MainFrame.this.dispose();
			System.exit(0);
		};
	};

	public MainFrame() {
		super("简易聊天软件");

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		inticomponent();
		JPanel pnl = getpanel();
		this.add(pnl);
		pack();

		setfieldintivalue();
		// 启动服务端socket监听器
		boolean result = startSeverLinstener();
		if (!result) {
			System.out.println("不能启动监听线程...");
		}
		// 添加窗口监听器
		this.addWindowListener(windowlistener);
		// 设置居中显示
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameDim = this.getSize();
		this.setLocation((screenDim.width - frameDim.width) / 2,
				(screenDim.height - frameDim.height) / 2);

	}

	/**
	 * 启动服务端socket监听器
	 */
	private boolean startSeverLinstener() {
		boolean result = true;
		try {
			serversocket = new ServerSocket(LINSTENER_PORT);
			listenerthread = new ServerListenerThread();
			listenerthread.start();
		} catch (IOException e) {
			// e.printStackTrace();
			result = false;
		}
		return result;
	}

	/**
	 * 设置控件的初始值
	 */
	private void setfieldintivalue() {
		try {
			// InetAddress address=InetAddress.getLocalHost();
			targetipfield.setText(InetAddress.getLocalHost().getHostAddress());
			targetportfield.setText(String.valueOf(LINSTENER_PORT));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 初始化面板
	 * 
	 * @return
	 */
	private JPanel getpanel() {
		JPanel pnl = new JPanel();
		pnl.setLayout(new GridBagLayout());

		GridBagConstraints cons = Utils.getGridBagconstraints(0, 0, 1, 1, 300,
				100, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		cons.insets = new Insets(5, 5, 5, 5);
		pnl.add(targetip, cons);

		cons = Utils.getGridBagconstraints(1, 0, 3, 1, 700, 100,
				GridBagConstraints.NONE, GridBagConstraints.CENTER);
		cons.insets = new Insets(5, 5, 5, 5);
		pnl.add(targetipfield, cons);

		cons = Utils.getGridBagconstraints(0, 1, 1, 1, 100, 100,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		cons.insets = new Insets(5, 5, 5, 5);
		pnl.add(targetport, cons);

		cons = Utils.getGridBagconstraints(1, 1, 1, 1, 700, 100,
				GridBagConstraints.NONE, GridBagConstraints.WEST);
		cons.insets = new Insets(5, 5, 5, 5);
		pnl.add(targetportfield, cons);

		cons = Utils.getGridBagconstraints(1, 2, 1, 1, 100, 100,
				GridBagConstraints.NONE, GridBagConstraints.WEST);
		cons.insets = new Insets(5, 5, 5, 5);
		pnl.add(button, cons);

		return pnl;
	}

	// 初始化组件
	private void inticomponent() {
		targetip = new JLabel("对方的ip:");
		targetipfield = new JTextField(16);
		// 添加校验功能(ip)
		targetipfield.setInputVerifier(new InputVerifier() {

			@Override
			public boolean verify(JComponent input) {
				boolean result = false;
				String ipString = targetipfield.getText().trim();
				if (ipString.length() > 0) {
					try {
						InetAddress.getByName(ipString);
						targetipfield.setBackground(Color.WHITE);
						result = true;
					} catch (UnknownHostException e) {
						targetipfield.setBackground(Color.blue);
						result = false;

					}

				} else {
					targetipfield.setBackground(Color.blue);
					return result = false;

				}
				return result;
			}
		});
		targetport = new JLabel("对方的端口");
		// 添加端口校验
//		targetportfield.setInputVerifier(new InputVerifier() {
//
//			@Override
//			public boolean verify(JComponent input) {
//				boolean result = false;
//				String temp = targetportfield.getText();
//				try {
//					Integer.parseInt(temp);
//					targetportfield.setBackground(Color.blue);
//					result = true;
//				} catch (Exception e) {
//					targetipfield.setBackground(Color.blue);
//					result = false;
//				}
//				return result;
//			}
//
//		});
		targetportfield = new JTextField(5);
		button = new JButton("开始聊天");
		button.addActionListener(starthandler);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final JFrame f = new MainFrame();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				f.setVisible(true);
			}
		});
	}

	/**
	 * 监听线程类
	 * 
	 * @author Administrator
	 * 
	 */
	private class ServerListenerThread extends Thread {
		private boolean running = true;

		public void stopThread() {
			running = false;
		}

		@Override
		public void run() {
			while (running) {
				try {
					Socket soc = serversocket.accept();
					// 弹出一个聊天窗口
					final TalkFrame f = new TalkFrame(MainFrame.this, soc);
					f.setLocation(500, 80);
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {

							MainFrame.this.setVisible(false);
							f.setVisible(true);
						}
					});

				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
		}
	}

}

package jp.silverbullet.remote.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SvSocketServer {

	private int port;
	private SvSocketHandler handler;
	
	public SvSocketServer(int port, SvSocketHandler handler2) {
		this.port = port;
		this.handler = handler2;
	}

	public void start() {
		new Thread() {
			@Override
			public void run() {
				startServer();
			}
		}.start();

	}

	protected void startServer() {
		// Remote thread
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {		
				Socket socket = serverSocket.accept();

				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream out = new PrintStream(socket.getOutputStream());
				
				String line;
				while ((line = in.readLine()) != null) {
					String ret = handler.onReceived(line);
					if (handler.isQuery(line)) {
						out.println(ret);
						out.flush();
						System.out.println("***REMOTE***   " + line + "->  ret:" + ret);
					}
				}
				socket.close();
				try {
					in.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					socket.close();
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

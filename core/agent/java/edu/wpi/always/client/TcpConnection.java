package edu.wpi.always.client;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class TcpConnection implements RemoteConnection {
	private final String hostname;
	private final int port;
	private Socket skt;
	private PrintWriter out;
	private BufferedReader in;
	private final ExecutorService sendThreadService;
	private final ExecutorService recvThreadService;
	private final ArrayList<TcpConnectionObserver> observers = new ArrayList<TcpConnectionObserver>();

	public TcpConnection (String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		sendThreadService = Executors.newSingleThreadExecutor();
		recvThreadService = Executors.newSingleThreadExecutor();
	}

	@Override
	public void connect () {
		try {
			skt = new Socket(hostname, port);
			out = new PrintWriter(skt.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(skt.getInputStream()));

			recvThreadService.execute(new Runnable() {
				@Override
				public void run () {
					char[] buf = new char[8192];
					int n;
					try {
						while ((n = in.read(buf)) != -1) {
							String s = new String(buf, 0, n);
							System.out.println();
							System.out.println("received: " + s);
							fireMessageReceived(s);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void fireMessageReceived (String s) {
		for (TcpConnectionObserver o : observers) {
			o.notifyMessageReceive(this, s);
		}
	}

	@Override
	public boolean isConnected () {
		return skt != null && skt.isConnected();
	}

	@Override
	public void beginSend (String message) {
		enqueue(message);
	}

	private void enqueue (String message) {
		final String msg = message;
		sendThreadService.execute(new Runnable() {
			@Override
			public void run () {
				send(msg);
			}
		});
	}

	private void send (String message) {
		if (!isConnected())
			connect();

		if (isConnected()) {
			System.out.println("sending... " + message);
			out.print(message);
			out.flush();
		}
	}

	@Override
	public void addObserver (TcpConnectionObserver o) {
		observers.add(o);
	}

	@Override
	public void removeObserver (TcpConnectionObserver o) {
		observers.remove(o);
	}
}
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
	
	private static final long serialVersionUID = 1;
	private Socket socket;
	private OutputStream ou;
	private Writer ouw;
	private BufferedWriter bfw;
	
	public void conectar(String ip, int porta) throws IOException{			
		
		socket = new Socket(ip, porta);
		ou = socket.getOutputStream();
		ouw = new OutputStreamWriter(ou);
		bfw = new BufferedWriter(ouw);
		bfw.write("Conectado\r\n");
		System.out.println("Conectado");
		
		bfw.flush();
		
	}
	
	public void enviarMensagem(String msg) throws IOException{
		
		if (msg.equals("Sair")) 
			bfw.write("Desconectado \r\n");
		else{
			bfw.write(msg + "\r\n");
			System.out.println("Enviando mensagem");
		}
		
		bfw.flush();
		
	}
	
	public void escutar() throws IOException {
		
		InputStream in = socket.getInputStream();
		InputStreamReader inr = new InputStreamReader(in);
		BufferedReader bfr = new BufferedReader(inr);
		String msg = "";
		
		while (!"Sair".equalsIgnoreCase(msg)){
			
			if (bfr.ready()) {
				msg = bfr.readLine();
				if (msg.equals("Sair"))
					System.out.println("Servidor caiu");
				else
					System.out.println(msg + "\r\n");
			}							
		}
	}
	
	public void sair() throws IOException {
		
		enviarMensagem("Sair");
		bfw.close();
		ouw.close();
		ou.close();
		socket.close();
	}
	
	public static void main(String[] args) throws IOException {
		
		Scanner sc = new Scanner(System.in);
		
		Cliente app = new Cliente();
		
		System.out.println("Informe o ip do servidor");
		String ip = sc.nextLine();
		
		sc.reset();
		
		System.out.println("Informe a porta do servidor");
		int porta = sc.nextInt();
		
		app.conectar(ip, porta);
		
		String msg = "";
		
		new Thread() {
			
			@Override
			public void run(){
				try {
					app.escutar();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}.start();
		
		while (true) {
			msg = "";
			sc.reset();
			msg = sc.nextLine();
			app.enviarMensagem(msg);
		}
	
	}
}

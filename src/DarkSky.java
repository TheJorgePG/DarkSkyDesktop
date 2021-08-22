import java.awt.EventQueue;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import manejador.Manejador;
import model.Currently;
import model.Daily;
import model.Data;
import model.Result;
import util.IconCellRenderer;
import util.Util;

public class DarkSky extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Currently currently;
	private static Daily daily;
	private JPanel contentPane;
	private static JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DarkSky frame = new DarkSky();
					frame.setVisible(true);
					getData();
					showData();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DarkSky() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(30, 30, 850, 400);
        contentPane.add(scrollPane);
		table = new JTable();
        /**
         * Truco para poner un JLabel en una celda de la tabla.
         * La necesitamos para poder poner el icono. 
         */
        table.setDefaultRenderer(Object.class, new IconCellRenderer());
        
        table.setRowHeight(64);
        table.setModel(new DefaultTableModel(new Object[][] { { null, null,
                null, null }, }, new String[] { "Fecha", "Pronostico",
                "Temperaturas", "Icono" }));
        scrollPane.setViewportView(table);

	}
	
	protected static void getData() {
		Manejador manejador= new Manejador("https://api.darksky.net/forecast/21259f9de3537b4f719c53580fa39c3a/39.8710026,-4.0251675?lang=es&exclude=minutely,hourly,alerts,flags");
		Result result = manejador.getResult();
		currently = result.getCurrently();
		daily = result.getDaily();
	}
	
	private static void showData() {
		
		String[] columnas = { "Fecha", "Pronostico", "Temperaturas", "Icono" };
		Object[][] datos = new Object[daily.getData().size()][4];
		for (int i = 0; i < daily.getData().size(); i++) {
			Data data = daily.getData().get(i);
			datos[i][0] = Util.timeSpanToDate(data.getTime());
			datos[i][1] = data.getSummary() + " (" + data.getPrecipProbability()
					+ "%)";
			datos[i][2] = "Max: " + Util.farToCel(data.getTemperatureMax()) + "º - Min: " + Util.farToCel(data.getTemperatureMin())
					+ "º";
			JLabel labelIcono = new JLabel();
			
			try {
				ImageIcon icon = new ImageIcon(new URL("https://darksky.net/images/weather-icons/"+data.getIcon()+".png"));
				Image image = icon.getImage();
				Image newimg = image.getScaledInstance(64, 64,  java.awt.Image.SCALE_DEFAULT);  
				icon = new ImageIcon(newimg);
				labelIcono.setIcon(icon);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			datos[i][3] = labelIcono;
			
		}
		table.setModel(new DefaultTableModel(datos, columnas));
	}
}

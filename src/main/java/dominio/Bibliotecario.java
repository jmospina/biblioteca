package dominio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.boot.model.source.spi.FetchableAttributeSource;

import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;

public class Bibliotecario {

	public static final String EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE = "El libro no se encuentra disponible";

	private RepositorioLibro repositorioLibro;
	private RepositorioPrestamo repositorioPrestamo;

	public Bibliotecario(RepositorioLibro repositorioLibro, RepositorioPrestamo repositorioPrestamo) {
		this.repositorioLibro = repositorioLibro;
		this.repositorioPrestamo = repositorioPrestamo;

	}
		
	public void prestar(String isbn, String nombreUsuario, String fechaPrestamo) {
		
		System.out.println("ISBN ingresado: " + isbn);
		
		boolean palindromo = verificarPalindromo(isbn);
		if (palindromo)
			throw new UnsupportedOperationException("Los libros palindromos solo se pueden utilizar en la biblioteca");
		
		Libro lib = this.repositorioLibro.obtenerPorIsbn(isbn);
		Libro libroPrestado = this.repositorioPrestamo.obtenerLibroPrestadoPorIsbn(isbn);		
		
		if (libroPrestado != null){
			System.out.println("El libro ya se encuentra prestado!");
			return;
		}			
		
		int suma = calcularSuma(isbn);		
		Date fechaEntrega = null;
		Date fechaSolicitud = null;
		
		if (fechaPrestamo.equals("") || fechaPrestamo == null){
			fechaSolicitud = new Date();
		}
		else{
			try {
				fechaSolicitud = new SimpleDateFormat("dd/MM/yyyy").parse(fechaPrestamo);
			} catch (ParseException e) {				
				e.printStackTrace();
			}  
		}			
		
		if (suma > 30) {
			fechaEntrega = calcularFechaEntrega(fechaSolicitud);
			System.out.println("Resultado fecha: "+ fechaEntrega);				
		}
		
		Prestamo prestamo = new Prestamo(fechaSolicitud, lib, fechaEntrega, nombreUsuario);		
		
		repositorioPrestamo.agregar(prestamo);
		
	}	
	
	public static Date calcularFechaEntrega(Date fechaSolicitud){
		Date FechaEntrega = new Date();
		final int numeroDias = 15;
		int contador = 1;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = Calendar.getInstance();
		cal.setTime(fechaSolicitud); 
		
		FechaEntrega = cal.getTime();
		
		String output = sdf.format(cal.getTime());
		System.out.println("fechaPrestamo: " + output);
		
		while (contador < numeroDias){
			cal.add(Calendar.DATE, 1); 
			
			if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
				contador++;
			
			if ((contador == 15) && (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)){
				cal.add(Calendar.DATE, 1);
			}
			
			FechaEntrega = cal.getTime();
		}
				
		System.out.println("FechaEntrega: " + sdf.format(cal.getTime()));
		
		return FechaEntrega;
	}
	
	public static int calcularSuma(String input){		
		int suma = 0;
		for(int i = 0; i < input.length(); i++){
	        final char c = input.charAt(i);
	        if(c > 47 && c < 58){	           
	            suma = suma + Character.getNumericValue(c);
	        }
	    }
		
		return suma;
	}
	
	public static  boolean verificarPalindromo(String input){
		int asc = 0;
		int des = input.length()-1;
		boolean respuesta = true;		
		
		while ((asc<des) && (respuesta)){
			
			if (input.charAt(asc) == input.charAt(des))
			{				
				asc++;
				des--;
			} 
			else 
			{
				respuesta = false;
			}		
		}
		
		return respuesta;
	}

	public boolean esPrestado(String isbn) {
		boolean respuesta = false;
		
		Libro lib = this.repositorioPrestamo.obtenerLibroPrestadoPorIsbn(isbn);
		if (lib != null)
			respuesta = true;
		
		return respuesta;
	}

}

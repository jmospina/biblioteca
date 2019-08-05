package dominio.unitaria;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import dominio.Bibliotecario;
import dominio.Libro;
import dominio.Prestamo;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.LibroTestDataBuilder;

public class BibliotecarioTest {

	@Test
	public void esPrestadoTest() {
		
		// arrange
		LibroTestDataBuilder libroTestDataBuilder = new LibroTestDataBuilder();
		
		Libro libro = libroTestDataBuilder.build(); 
		
		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);
		
		when(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn())).thenReturn(libro);
		
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);
		
		// act 
		boolean esPrestado =  bibliotecario.esPrestado(libro.getIsbn());
		
		//assert
		assertTrue(esPrestado);
	}
	
	@Test
	public void libroNoPrestadoTest() {
		
		// arrange
		LibroTestDataBuilder libroTestDataBuilder = new LibroTestDataBuilder();
		
		Libro libro = libroTestDataBuilder.build(); 
		
		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);
		
		when(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn())).thenReturn(null);
		
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);
		
		// act 
		boolean esPrestado =  bibliotecario.esPrestado(libro.getIsbn());
		
		//assert
		assertFalse(esPrestado);
	}
	
	@Test
	public void esPalindromo(){
		Libro libro1 = new Libro("abc121cba y abc121cba","Libro Palindromo 1",2019);
		Libro libro2 = new Libro("qerty ytre","Libro Palindromo 1",2019);
		boolean a = false;
		boolean b = false;
		a = Bibliotecario.verificarPalindromo(libro1.getIsbn());
		b = Bibliotecario.verificarPalindromo(libro2.getIsbn());
		if (a)
			System.out.println("El ISBN '" + libro1.getIsbn() + "' es Palindromo");
		else
			System.out.println("El ISBN '" + libro1.getIsbn() + "' NO es Palindromo");
		
		if (b)
			System.out.println("El ISBN '" + libro2.getIsbn() + "' es Palindromo");
		else
			System.out.println("El ISBN '" + libro2.getIsbn() + "' NO es Palindromo");
	}
	
	@Test
	public void calculoSuma(){
		Libro libro1 = new Libro("a45y78gk","Prueba suma",2018);
		int resultado = Bibliotecario.calcularSuma(libro1.getIsbn());
		
		System.out.println("El resultado de la suma de los digitos del ISBN es: " + resultado );
	}
	
	@Test
	public void calcularFechaEntregaMaxima(){
		String fecha = "26/05/2017";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = Calendar.getInstance();
		 
		try {
			Date d = Bibliotecario.calcularFechaEntrega(new SimpleDateFormat("dd/MM/yyyy").parse(fecha));
			cal.setTime(d);
		} catch (ParseException e) {			
			e.printStackTrace();
		}
		System.out.println("Fecha de prestamo: " + fecha + " Fecha de Entrega Maxima: " + sdf.format(cal.getTime()));
	}
	
	@Test 
	public void prestarTest(){		
		
		String nombreUsuario = "Pepito Perez";
		
		SistemaDePersistencia sistemaPersistencia = new SistemaDePersistencia();
		sistemaPersistencia.iniciar();
		
		RepositorioLibro repositorioLibros = sistemaPersistencia.obtenerRepositorioLibros();
		RepositorioPrestamo repositorioPrestamo = sistemaPersistencia.obtenerRepositorioPrestamos();
		Libro libro = new Libro("a323ab","Libro Test 1",2019);		
		repositorioLibros.agregar(libro);		
		sistemaPersistencia.terminar();		
				   
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		sistemaPersistencia.iniciar();
		bibliotecario.prestar(libro.getIsbn(), nombreUsuario, "19/03/2019");
		sistemaPersistencia.terminar();
		
		Libro l = repositorioLibros.obtenerPorIsbn(libro.getIsbn());					
		Prestamo p = repositorioPrestamo.obtener(libro.getIsbn());
		System.out.println("Libro en repositorio: " + l.getTitulo());
		System.out.println("Prestamo en repositorio: "+ p.getFechaSolicitud() + " " +p.getFechaEntregaMaxima() + " " +p.getNombreUsuario()+ " "+p.getLibro().getTitulo());	
		
	}
}

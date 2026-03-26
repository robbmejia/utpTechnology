package com.utp.technology.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.utp.technology.web.dto.product.ListProductoDto;
import com.utp.technology.web.dto.product.StoreProductDTO;
import com.utp.technology.model.Producto;
import com.utp.technology.repository.ProductoRepository;
import com.utp.technology.services.impl.ProductoServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

  @Mock
  private ProductoRepository productoRepository;

  @InjectMocks
  private ProductoServiceImpl productoService;

  private ListProductoDto productoDto;
  private Producto productoModel;
  private Pageable pageable;

  @BeforeEach
  void setUp() throws Exception {
    productoDto = new ListProductoDto();
    productoDto.setId(1);
    productoDto.setNombre("Test Product");
    productoDto.setImagen("test.png");
    productoDto.setPrecio(100.0);
    productoDto.setStock(10);

    productoModel = new Producto();
    productoModel.setId(1);
    productoModel.setNombre("Test Product");
    productoModel.setImagen("test.png");
    productoModel.setPrecio(100.0);
    productoModel.setStock(10);

    pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"));
    this.productoService = new ProductoServiceImpl(productoRepository);
  }

  @Test
  void listarProductos() throws Exception {
    String nombreBusqueda = "Test";
    List<Producto> productos = Collections.singletonList(productoModel);

    when(productoRepository.findAll()).thenReturn(productos);

    Page<ListProductoDto> resultPage = productoService.listAll(nombreBusqueda, pageable);

    assertThat(resultPage).isNotNull();
    assertThat(resultPage.getContent()).hasSize(1);
    assertThat(resultPage.getContent().get(0).getNombre()).isEqualTo("Test Product");

    verify(productoRepository, times(1)).findAll();
  }

  @Test
  void guardarProducto() throws Exception {
    when(productoRepository.save(any(Producto.class))).thenReturn(productoModel);

    StoreProductDTO dto = new StoreProductDTO();
    dto.setNombre(productoModel.getNombre());
    dto.setImagen(productoModel.getImagen());
    dto.setPrecio(productoModel.getPrecio());
    dto.setStock(productoModel.getStock());

    Producto result = productoService.guardar(dto);
    assertThat(result).isNotNull();
    assertThat(result.getNombre()).isEqualTo("Test Product");

    verify(productoRepository, times(1)).save(any(Producto.class));
  }

  @Test
  void editarProducto() throws Exception {
    when(productoRepository.save(any(Producto.class))).thenReturn(productoModel);

    Producto result = productoService.editar(productoModel);
    assertThat(result).isNotNull();
    verify(productoRepository, times(1)).save(any(Producto.class));
  }

  @Test
  void eliminarProducto() throws Exception {
    productoService.eliminar(productoModel);
    verify(productoRepository, times(1)).delete(any(Producto.class));
  }
}

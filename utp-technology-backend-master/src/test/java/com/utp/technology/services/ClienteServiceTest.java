package com.utp.technology.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import com.utp.technology.model.Cliente;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.utp.technology.web.dto.cliente.ListClienteDto;
import com.utp.technology.repository.ClienteRepository;
import com.utp.technology.services.impl.ClienteServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {
  @Mock
  private ClienteRepository clienteRepository;

  private ClienteServiceImpl clienteService;

  private Pageable pageable;

  @BeforeEach
  void setUp() {
    pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"));
    this.clienteService = new ClienteServiceImpl(clienteRepository);
  }

  @Test
  void listarClientes() throws Exception {

    String searchTerm = null;

    // Al llamar al a función listCliente, se devolvera la página de arriba
    when(clienteRepository.findAll()).thenReturn(Collections.singletonList(new Cliente()));

    Page<ListClienteDto> resultPage = clienteService.listCliente(searchTerm, pageable);

    // Validando que resultPage no sea null
    assertThat(resultPage).isNotNull();
    // Validando que el resultado tenga un tamaño de 1
    assertThat(resultPage.getContent()).hasSize(1);

    // Validando que el método fue correctamente invocado
    verify(clienteRepository, times(1)).findAll();
  }

}

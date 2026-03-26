package com.utp.technology.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.utp.technology.web.dto.auth.SignUpRequestDTO;
import com.utp.technology.web.dto.user.UserListDto;
import com.utp.technology.model.Cliente;
import com.utp.technology.model.Usuario;
import com.utp.technology.repository.ClienteRepository;
import com.utp.technology.repository.UsuarioRepository;
import com.utp.technology.services.impl.UsuarioServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

  @Mock
  private UsuarioRepository usuarioRepository;

  @Mock
  private ClienteRepository clienteRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UsuarioServiceImpl usuarioService;

  private UserListDto userDto;
  private Usuario userModel;
  private Cliente cliente;

  @BeforeEach
  void setUp() throws Exception {
    this.userDto = new UserListDto();
    this.userDto.setId(1);
    this.userDto.setNombre("Test User");
    this.userDto.setCorreo("test@gmail.com");

    this.userModel = new Usuario();
    this.userModel.setId(1);
    this.userModel.setClave("123456");
    this.userModel.setCorreo("test@gmail.com");
    this.userModel.setNombre("Test User");

    this.cliente = new Cliente();
    this.cliente.setId(1);
    this.cliente.setApellido("Apellido");
    this.cliente.setCorreo("test@gmail.com");
    this.cliente.setDni("12345678");

    this.userModel.setId_rol(1);

    this.usuarioService = new UsuarioServiceImpl(usuarioRepository, clienteRepository, passwordEncoder);
  }

  @Test
  void testListAll() throws Exception {
    when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(userModel));

    List<UserListDto> result = usuarioService.listAll();

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCorreo()).isEqualTo("test@gmail.com");

    verify(usuarioRepository, times(1)).findAll();
  }

  @Test
  void testFindById() throws Exception {
    Integer userId = 1;
    when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(userModel));

    Optional<Usuario> usuarioOpt = usuarioService.findById(userId);

    assertThat(usuarioOpt.isPresent()).isEqualTo(true);

    verify(usuarioRepository, times(1)).findAll();
  }

  @Test
  void testFindByCorreo() throws Exception {
    String userCorreo = userModel.getCorreo();
    when(usuarioRepository.findByCorreo(eq(userCorreo))).thenReturn(Optional.of(userModel));

    Optional<Usuario> usuarioOpt = usuarioService.findByCorreo(userCorreo);

    assertThat(usuarioOpt.isPresent()).isEqualTo(true);
    assertThat(usuarioOpt.get().getCorreo()).isEqualTo(userModel.getCorreo());

    verify(usuarioRepository, times(1)).findByCorreo(userCorreo);
  }

  @Test
  void testRegistrarUsuarioNoExistente() throws Exception {
    SignUpRequestDTO dto = new SignUpRequestDTO();
    dto.setDni("12345678");
    dto.setApellido("Apellido");
    dto.setEmail("test@gmail.com");
    dto.setNombre("Test User");
    dto.setPassword("123456");
    dto.setTelefono("999888777");

    when(clienteRepository.findByDni(eq(dto.getDni()))).thenReturn(Optional.empty());
    when(passwordEncoder.encode(eq(dto.getPassword()))).thenReturn(dto.getPassword());
    when(usuarioRepository.save(any(Usuario.class))).thenReturn(userModel);
    when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

    Usuario result = this.usuarioService.registrarUsuario(dto);

    assertThat(result).isNotNull();
    verify(usuarioRepository, times(1)).save(any(Usuario.class));
    verify(clienteRepository, times(1)).save(any(Cliente.class));
  }

}

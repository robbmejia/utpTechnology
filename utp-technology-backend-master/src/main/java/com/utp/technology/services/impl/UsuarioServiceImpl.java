package com.utp.technology.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.utp.technology.web.dto.auth.SignUpRequestDTO;
import com.utp.technology.web.dto.user.UserListDto;
import com.utp.technology.model.Cliente;
import com.utp.technology.model.Usuario;
import com.utp.technology.repository.ClienteRepository;
import com.utp.technology.repository.UsuarioRepository;
import com.utp.technology.services.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final ClienteRepository clienteRepository;
  private final PasswordEncoder passwordEncoder;

  public UsuarioServiceImpl(UsuarioRepository usuarioRepository, ClienteRepository clienteRepository,
      PasswordEncoder passwordEncoder) {
    this.usuarioRepository = usuarioRepository;
    this.clienteRepository = clienteRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public List<UserListDto> listAll() {
    try {
      return this.usuarioRepository.findAll().stream()
          .map(u -> {
            UserListDto dto = new UserListDto();
            dto.setId(u.getId());
            dto.setNombre(u.getNombre());
            dto.setCorreo(u.getCorreo());
            dto.setId_rol(u.getId_rol());
            return dto;
          })
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new RuntimeException("Error al listar usuarios", e);
    }
  }

  @Override
  public Optional<Usuario> findById(Integer id) {
    try {
      return this.usuarioRepository.findById(id);
    } catch (Exception e) {
      throw new RuntimeException("Error al buscar usuario por id", e);
    }
  }

  @Override
  public Optional<Usuario> findByCorreo(String correo) {
    try {
      return this.usuarioRepository.findByCorreo(correo);
    } catch (Exception e) {
      throw new RuntimeException("Error al buscar usuario por correo", e);
    }
  }

  @Override
  public Usuario registrarUsuario(SignUpRequestDTO data) {
    try {
      if (this.clienteRepository.findByDni(data.getDni()).isPresent()) {
        throw new IllegalArgumentException("El dni ya esta siendo usado.");
      }

      Usuario usuario = new Usuario();
      usuario.setNombre(data.getNombre());
      usuario.setCorreo(data.getEmail());
      usuario.setClave(this.passwordEncoder.encode(data.getPassword()));
      usuario.setId_rol(3); // Default USER role

      usuario = this.usuarioRepository.save(usuario);

      Cliente cliente = new Cliente();
      cliente.setNombre(data.getNombre());
      cliente.setApellido(data.getApellido());
      cliente.setDni(data.getDni());
      cliente.setTelefono(data.getTelefono());
      cliente.setCorreo(data.getEmail());

      this.clienteRepository.save(cliente);

      return usuario;
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Error al registrar usuario", e);
    }
  }

  @Override
  public boolean checkPassword(String hashedPassword, String password) {
    if (hashedPassword != null && hashedPassword.equals(password)) {
      return true;
    }
    if (hashedPassword == null || password == null) {
      return false;
    }
    try {
      return this.passwordEncoder.matches(password, hashedPassword);
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public Usuario guardar(Usuario usuario) {
    if (usuario.getClave() != null && !usuario.getClave().isEmpty()) {
      usuario.setClave(this.passwordEncoder.encode(usuario.getClave()));
    }
    return this.usuarioRepository.save(usuario);
  }

  @Override
  public Usuario editar(Integer id, Usuario usuario) {
    Optional<Usuario> existingOpt = this.findById(id);
    if (existingOpt.isEmpty()) {
      throw new RuntimeException("Usuario no encontrado");
    }
    Usuario existing = existingOpt.get();

    // Súper Administrador Protegido
    if ("admin@tienda.com".equals(existing.getCorreo())) {
      throw new RuntimeException("Las credenciales del Administrador Principal están protegidas y no pueden ser editadas por ningún usuario.");
    }

    existing.setNombre(usuario.getNombre());
    existing.setCorreo(usuario.getCorreo());
    existing.setId_rol(usuario.getId_rol());
    if (usuario.getClave() != null && !usuario.getClave().trim().isEmpty()) {
      existing.setClave(this.passwordEncoder.encode(usuario.getClave()));
    }
    return this.usuarioRepository.save(existing);
  }

  @Override
  public void eliminar(Integer id) {
    Optional<Usuario> existingOpt = this.findById(id);
    if (existingOpt.isPresent()) {
      Usuario existing = existingOpt.get();
      if ("admin@tienda.com".equals(existing.getCorreo()) && existing.getId_rol() != null
          && existing.getId_rol() == 1) {
        throw new RuntimeException("Protección Crítica: No se permite eliminar al Administrador Principal.");
      }
    }
    this.usuarioRepository.deleteById(id);
  }

  @Override
  public int cleanAnomalies() {
    try {
      return this.usuarioRepository.cleanAnomalies();
    } catch (Exception e) {
      throw new RuntimeException("Error limpiando anomalías", e);
    }
  }

}

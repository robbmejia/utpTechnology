package com.utp.technology.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.utp.technology.model.Cliente;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ClienteRepository {

  private final Firestore firestore;
  private final String COLLECTION_NAME = "clientes";

  public ClienteRepository(Firestore firestore) {
    this.firestore = firestore;
  }

  public Optional<Cliente> findByUsuarioId(Integer idUsuario) throws ExecutionException, InterruptedException {
    ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME)
        .whereEqualTo("idUsuario", idUsuario)
        .get();
    QuerySnapshot querySnapshot = query.get();
    return querySnapshot.getDocuments().stream()
        .findFirst()
        .map(doc -> doc.toObject(Cliente.class));
  }

  public Optional<Cliente> findByDni(String dni) throws ExecutionException, InterruptedException {
    ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME)
        .whereEqualTo("dni", dni)
        .get();
    QuerySnapshot querySnapshot = query.get();
    return querySnapshot.getDocuments().stream()
        .findFirst()
        .map(doc -> doc.toObject(Cliente.class));
  }

  public List<Cliente> findAll() throws ExecutionException, InterruptedException {
    ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME).get();
    QuerySnapshot querySnapshot = query.get();
    return querySnapshot.getDocuments().stream()
        .map(doc -> doc.toObject(Cliente.class))
        .collect(Collectors.toList());
  }

  public Optional<Cliente> findById(Integer id) throws ExecutionException, InterruptedException {
    ApiFuture<DocumentSnapshot> future = firestore.collection(COLLECTION_NAME).document(String.valueOf(id)).get();
    DocumentSnapshot document = future.get();
    if (document.exists()) {
      return Optional.ofNullable(document.toObject(Cliente.class));
    }
    return Optional.empty();
  }

  public Cliente save(Cliente cliente) {
    if (cliente.getId() == null) {
      cliente.setId(Math.abs((int) System.currentTimeMillis() + (int) (Math.random() * 10000)));
      firestore.collection(COLLECTION_NAME).document(String.valueOf(cliente.getId())).set(cliente);
    } else {
      firestore.collection(COLLECTION_NAME).document(String.valueOf(cliente.getId())).set(cliente);
    }
    return cliente;
  }
}

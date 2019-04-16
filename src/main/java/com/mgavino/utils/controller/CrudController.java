package com.mgavino.utils.controller;

import com.mgavino.utils.model.IdentifyEntity;
import com.mgavino.utils.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

public abstract class CrudController<E extends IdentifyEntity> {

    @Autowired
    private MongoRepository<E, String> repository;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<E>> getAll() {

        List<E> entities = repository.findAll();
        if (entities == null || entities.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<List<E>>(entities, HttpStatus.OK);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<E> get(@PathVariable("id") String id) {

        Optional<E> entity = repository.findById(id);
        if (!entity.isPresent()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<E>(entity.get(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<E> post(@RequestBody E entity, UriComponentsBuilder ucBuilder) {

        if (entity.getId() != null) {
            entity.setId(null);
        }

        entity = repository.save(entity);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/example/{id}").buildAndExpand(entity.getId()).toUri());
        return new ResponseEntity<E>(entity, headers, HttpStatus.CREATED);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<E> put(@PathVariable("id") String id, @RequestBody E entity) {

        Optional<E> foundEntity = repository.findById(id);
        if (!foundEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        entity.setId(id);
        repository.save(entity);

        return new ResponseEntity<E>(entity, HttpStatus.OK);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<E> patch(@PathVariable("id") String id, @RequestBody E entity) {

        Optional<E> foundEntity = repository.findById(id);
        if (!foundEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (entity.getId() != null) {
            entity.setId(null);
        }

        E finalEntity = foundEntity.get();
        Utils.merge( entity, finalEntity );

        repository.save( finalEntity );

        return new ResponseEntity<E>(entity, HttpStatus.OK);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<E> delete(@PathVariable("id") String id) {

        Optional<E> entity = repository.findById(id);
        if (!entity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        repository.deleteById(id);
        return new ResponseEntity<E>(HttpStatus.NO_CONTENT);
    }

}

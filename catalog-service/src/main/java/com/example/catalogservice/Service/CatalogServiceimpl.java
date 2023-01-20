package com.example.catalogservice.Service;

import com.example.catalogservice.jpa.CatalogEntity;
import com.example.catalogservice.jpa.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


@Service
public class CatalogServiceimpl implements CatalogService{

    CatalogRepository catalogRepository;
    Environment env;

    @Autowired
    public CatalogServiceimpl(CatalogRepository catalogRepository, Environment env) {
        this.catalogRepository = catalogRepository;
        this.env = env;
    }

    public CatalogServiceimpl(Environment env) {
        this.env = env;
    }

    @Override
    public Iterable<CatalogEntity> getAllCatalogs() {
        return catalogRepository.findAll();
    }
}

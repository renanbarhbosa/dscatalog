package com.renanbarhbosa.dscatalog.resources;

import com.renanbarhbosa.dscatalog.dto.ProductDTO;
import com.renanbarhbosa.dscatalog.services.ProductService;
import com.renanbarhbosa.dscatalog.services.exceptions.ResourceNotFoundException;
import factory.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductService service;
    private Long existingId;
    private Long nonExistingId;
    private ProductDTO productDTO;
    private PageImpl<ProductDTO> page;

    @BeforeEach
    void setUp() throws Exception {

        existingId = 1L;

        nonExistingId = 2L;

        productDTO = Factory.createProductDTO();

        page = new PageImpl<>(List.of(productDTO));

        when(service.findAllPaged(any())).thenReturn(page);

        when(service.findById(existingId)).thenReturn(productDTO);

        when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        ResultActions res = mockMvc.perform(get("/products")
                .accept(MediaType.APPLICATION_JSON));
        res.andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {
        ResultActions res = mockMvc.perform(get("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));
        res.andExpect(jsonPath("$.id").exists());
        res.andExpect(jsonPath("$.name").exists());
        res.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions res = mockMvc.perform(get("/products/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON));
        res.andExpect(status().isNotFound());
    }
}
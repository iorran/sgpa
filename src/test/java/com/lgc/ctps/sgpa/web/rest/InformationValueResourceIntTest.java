package com.lgc.ctps.sgpa.web.rest;

import com.lgc.ctps.sgpa.SgpaApp;

import com.lgc.ctps.sgpa.domain.InformationValue;
import com.lgc.ctps.sgpa.repository.InformationValueRepository;
import com.lgc.ctps.sgpa.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.lgc.ctps.sgpa.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the InformationValueResource REST controller.
 *
 * @see InformationValueResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SgpaApp.class)
public class InformationValueResourceIntTest {

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    @Autowired
    private InformationValueRepository informationValueRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restInformationValueMockMvc;

    private InformationValue informationValue;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final InformationValueResource informationValueResource = new InformationValueResource(informationValueRepository);
        this.restInformationValueMockMvc = MockMvcBuilders.standaloneSetup(informationValueResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InformationValue createEntity(EntityManager em) {
        InformationValue informationValue = new InformationValue()
            .value(DEFAULT_VALUE);
        return informationValue;
    }

    @Before
    public void initTest() {
        informationValue = createEntity(em);
    }

    @Test
    @Transactional
    public void createInformationValue() throws Exception {
        int databaseSizeBeforeCreate = informationValueRepository.findAll().size();

        // Create the InformationValue
        restInformationValueMockMvc.perform(post("/api/information-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(informationValue)))
            .andExpect(status().isCreated());

        // Validate the InformationValue in the database
        List<InformationValue> informationValueList = informationValueRepository.findAll();
        assertThat(informationValueList).hasSize(databaseSizeBeforeCreate + 1);
        InformationValue testInformationValue = informationValueList.get(informationValueList.size() - 1);
        assertThat(testInformationValue.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createInformationValueWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = informationValueRepository.findAll().size();

        // Create the InformationValue with an existing ID
        informationValue.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restInformationValueMockMvc.perform(post("/api/information-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(informationValue)))
            .andExpect(status().isBadRequest());

        // Validate the InformationValue in the database
        List<InformationValue> informationValueList = informationValueRepository.findAll();
        assertThat(informationValueList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = informationValueRepository.findAll().size();
        // set the field null
        informationValue.setValue(null);

        // Create the InformationValue, which fails.

        restInformationValueMockMvc.perform(post("/api/information-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(informationValue)))
            .andExpect(status().isBadRequest());

        List<InformationValue> informationValueList = informationValueRepository.findAll();
        assertThat(informationValueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllInformationValues() throws Exception {
        // Initialize the database
        informationValueRepository.saveAndFlush(informationValue);

        // Get all the informationValueList
        restInformationValueMockMvc.perform(get("/api/information-values?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(informationValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }

    @Test
    @Transactional
    public void getInformationValue() throws Exception {
        // Initialize the database
        informationValueRepository.saveAndFlush(informationValue);

        // Get the informationValue
        restInformationValueMockMvc.perform(get("/api/information-values/{id}", informationValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(informationValue.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingInformationValue() throws Exception {
        // Get the informationValue
        restInformationValueMockMvc.perform(get("/api/information-values/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateInformationValue() throws Exception {
        // Initialize the database
        informationValueRepository.saveAndFlush(informationValue);
        int databaseSizeBeforeUpdate = informationValueRepository.findAll().size();

        // Update the informationValue
        InformationValue updatedInformationValue = informationValueRepository.findOne(informationValue.getId());
        // Disconnect from session so that the updates on updatedInformationValue are not directly saved in db
        em.detach(updatedInformationValue);
        updatedInformationValue
            .value(UPDATED_VALUE);

        restInformationValueMockMvc.perform(put("/api/information-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedInformationValue)))
            .andExpect(status().isOk());

        // Validate the InformationValue in the database
        List<InformationValue> informationValueList = informationValueRepository.findAll();
        assertThat(informationValueList).hasSize(databaseSizeBeforeUpdate);
        InformationValue testInformationValue = informationValueList.get(informationValueList.size() - 1);
        assertThat(testInformationValue.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void updateNonExistingInformationValue() throws Exception {
        int databaseSizeBeforeUpdate = informationValueRepository.findAll().size();

        // Create the InformationValue

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restInformationValueMockMvc.perform(put("/api/information-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(informationValue)))
            .andExpect(status().isCreated());

        // Validate the InformationValue in the database
        List<InformationValue> informationValueList = informationValueRepository.findAll();
        assertThat(informationValueList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteInformationValue() throws Exception {
        // Initialize the database
        informationValueRepository.saveAndFlush(informationValue);
        int databaseSizeBeforeDelete = informationValueRepository.findAll().size();

        // Get the informationValue
        restInformationValueMockMvc.perform(delete("/api/information-values/{id}", informationValue.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<InformationValue> informationValueList = informationValueRepository.findAll();
        assertThat(informationValueList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InformationValue.class);
        InformationValue informationValue1 = new InformationValue();
        informationValue1.setId(1L);
        InformationValue informationValue2 = new InformationValue();
        informationValue2.setId(informationValue1.getId());
        assertThat(informationValue1).isEqualTo(informationValue2);
        informationValue2.setId(2L);
        assertThat(informationValue1).isNotEqualTo(informationValue2);
        informationValue1.setId(null);
        assertThat(informationValue1).isNotEqualTo(informationValue2);
    }
}

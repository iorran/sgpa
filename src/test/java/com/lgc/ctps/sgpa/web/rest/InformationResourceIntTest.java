package com.lgc.ctps.sgpa.web.rest;

import com.lgc.ctps.sgpa.SgpaApp;

import com.lgc.ctps.sgpa.domain.Information;
import com.lgc.ctps.sgpa.repository.InformationRepository;
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
 * Test class for the InformationResource REST controller.
 *
 * @see InformationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SgpaApp.class)
public class InformationResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private InformationRepository informationRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restInformationMockMvc;

    private Information information;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final InformationResource informationResource = new InformationResource(informationRepository);
        this.restInformationMockMvc = MockMvcBuilders.standaloneSetup(informationResource)
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
    public static Information createEntity(EntityManager em) {
        Information information = new Information()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION);
        return information;
    }

    @Before
    public void initTest() {
        information = createEntity(em);
    }

    @Test
    @Transactional
    public void createInformation() throws Exception {
        int databaseSizeBeforeCreate = informationRepository.findAll().size();

        // Create the Information
        restInformationMockMvc.perform(post("/api/information")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(information)))
            .andExpect(status().isCreated());

        // Validate the Information in the database
        List<Information> informationList = informationRepository.findAll();
        assertThat(informationList).hasSize(databaseSizeBeforeCreate + 1);
        Information testInformation = informationList.get(informationList.size() - 1);
        assertThat(testInformation.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testInformation.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createInformationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = informationRepository.findAll().size();

        // Create the Information with an existing ID
        information.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restInformationMockMvc.perform(post("/api/information")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(information)))
            .andExpect(status().isBadRequest());

        // Validate the Information in the database
        List<Information> informationList = informationRepository.findAll();
        assertThat(informationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = informationRepository.findAll().size();
        // set the field null
        information.setName(null);

        // Create the Information, which fails.

        restInformationMockMvc.perform(post("/api/information")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(information)))
            .andExpect(status().isBadRequest());

        List<Information> informationList = informationRepository.findAll();
        assertThat(informationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllInformation() throws Exception {
        // Initialize the database
        informationRepository.saveAndFlush(information);

        // Get all the informationList
        restInformationMockMvc.perform(get("/api/information?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(information.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getInformation() throws Exception {
        // Initialize the database
        informationRepository.saveAndFlush(information);

        // Get the information
        restInformationMockMvc.perform(get("/api/information/{id}", information.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(information.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingInformation() throws Exception {
        // Get the information
        restInformationMockMvc.perform(get("/api/information/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateInformation() throws Exception {
        // Initialize the database
        informationRepository.saveAndFlush(information);
        int databaseSizeBeforeUpdate = informationRepository.findAll().size();

        // Update the information
        Information updatedInformation = informationRepository.findOne(information.getId());
        // Disconnect from session so that the updates on updatedInformation are not directly saved in db
        em.detach(updatedInformation);
        updatedInformation
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION);

        restInformationMockMvc.perform(put("/api/information")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedInformation)))
            .andExpect(status().isOk());

        // Validate the Information in the database
        List<Information> informationList = informationRepository.findAll();
        assertThat(informationList).hasSize(databaseSizeBeforeUpdate);
        Information testInformation = informationList.get(informationList.size() - 1);
        assertThat(testInformation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testInformation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingInformation() throws Exception {
        int databaseSizeBeforeUpdate = informationRepository.findAll().size();

        // Create the Information

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restInformationMockMvc.perform(put("/api/information")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(information)))
            .andExpect(status().isCreated());

        // Validate the Information in the database
        List<Information> informationList = informationRepository.findAll();
        assertThat(informationList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteInformation() throws Exception {
        // Initialize the database
        informationRepository.saveAndFlush(information);
        int databaseSizeBeforeDelete = informationRepository.findAll().size();

        // Get the information
        restInformationMockMvc.perform(delete("/api/information/{id}", information.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Information> informationList = informationRepository.findAll();
        assertThat(informationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Information.class);
        Information information1 = new Information();
        information1.setId(1L);
        Information information2 = new Information();
        information2.setId(information1.getId());
        assertThat(information1).isEqualTo(information2);
        information2.setId(2L);
        assertThat(information1).isNotEqualTo(information2);
        information1.setId(null);
        assertThat(information1).isNotEqualTo(information2);
    }
}

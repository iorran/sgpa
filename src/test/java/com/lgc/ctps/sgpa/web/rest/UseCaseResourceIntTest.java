package com.lgc.ctps.sgpa.web.rest;

import com.lgc.ctps.sgpa.SgpaApp;

import com.lgc.ctps.sgpa.domain.UseCase;
import com.lgc.ctps.sgpa.repository.UseCaseRepository;
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
 * Test class for the UseCaseResource REST controller.
 *
 * @see UseCaseResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SgpaApp.class)
public class UseCaseResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private UseCaseRepository useCaseRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restUseCaseMockMvc;

    private UseCase useCase;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UseCaseResource useCaseResource = new UseCaseResource(useCaseRepository);
        this.restUseCaseMockMvc = MockMvcBuilders.standaloneSetup(useCaseResource)
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
    public static UseCase createEntity(EntityManager em) {
        UseCase useCase = new UseCase()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION);
        return useCase;
    }

    @Before
    public void initTest() {
        useCase = createEntity(em);
    }

    @Test
    @Transactional
    public void createUseCase() throws Exception {
        int databaseSizeBeforeCreate = useCaseRepository.findAll().size();

        // Create the UseCase
        restUseCaseMockMvc.perform(post("/api/use-cases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(useCase)))
            .andExpect(status().isCreated());

        // Validate the UseCase in the database
        List<UseCase> useCaseList = useCaseRepository.findAll();
        assertThat(useCaseList).hasSize(databaseSizeBeforeCreate + 1);
        UseCase testUseCase = useCaseList.get(useCaseList.size() - 1);
        assertThat(testUseCase.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUseCase.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createUseCaseWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = useCaseRepository.findAll().size();

        // Create the UseCase with an existing ID
        useCase.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUseCaseMockMvc.perform(post("/api/use-cases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(useCase)))
            .andExpect(status().isBadRequest());

        // Validate the UseCase in the database
        List<UseCase> useCaseList = useCaseRepository.findAll();
        assertThat(useCaseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = useCaseRepository.findAll().size();
        // set the field null
        useCase.setName(null);

        // Create the UseCase, which fails.

        restUseCaseMockMvc.perform(post("/api/use-cases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(useCase)))
            .andExpect(status().isBadRequest());

        List<UseCase> useCaseList = useCaseRepository.findAll();
        assertThat(useCaseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllUseCases() throws Exception {
        // Initialize the database
        useCaseRepository.saveAndFlush(useCase);

        // Get all the useCaseList
        restUseCaseMockMvc.perform(get("/api/use-cases?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(useCase.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getUseCase() throws Exception {
        // Initialize the database
        useCaseRepository.saveAndFlush(useCase);

        // Get the useCase
        restUseCaseMockMvc.perform(get("/api/use-cases/{id}", useCase.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(useCase.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingUseCase() throws Exception {
        // Get the useCase
        restUseCaseMockMvc.perform(get("/api/use-cases/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUseCase() throws Exception {
        // Initialize the database
        useCaseRepository.saveAndFlush(useCase);
        int databaseSizeBeforeUpdate = useCaseRepository.findAll().size();

        // Update the useCase
        UseCase updatedUseCase = useCaseRepository.findOne(useCase.getId());
        // Disconnect from session so that the updates on updatedUseCase are not directly saved in db
        em.detach(updatedUseCase);
        updatedUseCase
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION);

        restUseCaseMockMvc.perform(put("/api/use-cases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedUseCase)))
            .andExpect(status().isOk());

        // Validate the UseCase in the database
        List<UseCase> useCaseList = useCaseRepository.findAll();
        assertThat(useCaseList).hasSize(databaseSizeBeforeUpdate);
        UseCase testUseCase = useCaseList.get(useCaseList.size() - 1);
        assertThat(testUseCase.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUseCase.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingUseCase() throws Exception {
        int databaseSizeBeforeUpdate = useCaseRepository.findAll().size();

        // Create the UseCase

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restUseCaseMockMvc.perform(put("/api/use-cases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(useCase)))
            .andExpect(status().isCreated());

        // Validate the UseCase in the database
        List<UseCase> useCaseList = useCaseRepository.findAll();
        assertThat(useCaseList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteUseCase() throws Exception {
        // Initialize the database
        useCaseRepository.saveAndFlush(useCase);
        int databaseSizeBeforeDelete = useCaseRepository.findAll().size();

        // Get the useCase
        restUseCaseMockMvc.perform(delete("/api/use-cases/{id}", useCase.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<UseCase> useCaseList = useCaseRepository.findAll();
        assertThat(useCaseList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UseCase.class);
        UseCase useCase1 = new UseCase();
        useCase1.setId(1L);
        UseCase useCase2 = new UseCase();
        useCase2.setId(useCase1.getId());
        assertThat(useCase1).isEqualTo(useCase2);
        useCase2.setId(2L);
        assertThat(useCase1).isNotEqualTo(useCase2);
        useCase1.setId(null);
        assertThat(useCase1).isNotEqualTo(useCase2);
    }
}

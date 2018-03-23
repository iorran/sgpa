package com.lgc.ctps.sgpa.web.rest;

import com.lgc.ctps.sgpa.SgpaApp;

import com.lgc.ctps.sgpa.domain.PersonRole;
import com.lgc.ctps.sgpa.repository.PersonRoleRepository;
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
 * Test class for the PersonRoleResource REST controller.
 *
 * @see PersonRoleResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SgpaApp.class)
public class PersonRoleResourceIntTest {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    @Autowired
    private PersonRoleRepository personRoleRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPersonRoleMockMvc;

    private PersonRole personRole;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PersonRoleResource personRoleResource = new PersonRoleResource(personRoleRepository);
        this.restPersonRoleMockMvc = MockMvcBuilders.standaloneSetup(personRoleResource)
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
    public static PersonRole createEntity(EntityManager em) {
        PersonRole personRole = new PersonRole()
            .key(DEFAULT_KEY);
        return personRole;
    }

    @Before
    public void initTest() {
        personRole = createEntity(em);
    }

    @Test
    @Transactional
    public void createPersonRole() throws Exception {
        int databaseSizeBeforeCreate = personRoleRepository.findAll().size();

        // Create the PersonRole
        restPersonRoleMockMvc.perform(post("/api/person-roles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(personRole)))
            .andExpect(status().isCreated());

        // Validate the PersonRole in the database
        List<PersonRole> personRoleList = personRoleRepository.findAll();
        assertThat(personRoleList).hasSize(databaseSizeBeforeCreate + 1);
        PersonRole testPersonRole = personRoleList.get(personRoleList.size() - 1);
        assertThat(testPersonRole.getKey()).isEqualTo(DEFAULT_KEY);
    }

    @Test
    @Transactional
    public void createPersonRoleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = personRoleRepository.findAll().size();

        // Create the PersonRole with an existing ID
        personRole.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPersonRoleMockMvc.perform(post("/api/person-roles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(personRole)))
            .andExpect(status().isBadRequest());

        // Validate the PersonRole in the database
        List<PersonRole> personRoleList = personRoleRepository.findAll();
        assertThat(personRoleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = personRoleRepository.findAll().size();
        // set the field null
        personRole.setKey(null);

        // Create the PersonRole, which fails.

        restPersonRoleMockMvc.perform(post("/api/person-roles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(personRole)))
            .andExpect(status().isBadRequest());

        List<PersonRole> personRoleList = personRoleRepository.findAll();
        assertThat(personRoleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPersonRoles() throws Exception {
        // Initialize the database
        personRoleRepository.saveAndFlush(personRole);

        // Get all the personRoleList
        restPersonRoleMockMvc.perform(get("/api/person-roles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(personRole.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY.toString())));
    }

    @Test
    @Transactional
    public void getPersonRole() throws Exception {
        // Initialize the database
        personRoleRepository.saveAndFlush(personRole);

        // Get the personRole
        restPersonRoleMockMvc.perform(get("/api/person-roles/{id}", personRole.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(personRole.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPersonRole() throws Exception {
        // Get the personRole
        restPersonRoleMockMvc.perform(get("/api/person-roles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePersonRole() throws Exception {
        // Initialize the database
        personRoleRepository.saveAndFlush(personRole);
        int databaseSizeBeforeUpdate = personRoleRepository.findAll().size();

        // Update the personRole
        PersonRole updatedPersonRole = personRoleRepository.findOne(personRole.getId());
        // Disconnect from session so that the updates on updatedPersonRole are not directly saved in db
        em.detach(updatedPersonRole);
        updatedPersonRole
            .key(UPDATED_KEY);

        restPersonRoleMockMvc.perform(put("/api/person-roles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPersonRole)))
            .andExpect(status().isOk());

        // Validate the PersonRole in the database
        List<PersonRole> personRoleList = personRoleRepository.findAll();
        assertThat(personRoleList).hasSize(databaseSizeBeforeUpdate);
        PersonRole testPersonRole = personRoleList.get(personRoleList.size() - 1);
        assertThat(testPersonRole.getKey()).isEqualTo(UPDATED_KEY);
    }

    @Test
    @Transactional
    public void updateNonExistingPersonRole() throws Exception {
        int databaseSizeBeforeUpdate = personRoleRepository.findAll().size();

        // Create the PersonRole

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPersonRoleMockMvc.perform(put("/api/person-roles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(personRole)))
            .andExpect(status().isCreated());

        // Validate the PersonRole in the database
        List<PersonRole> personRoleList = personRoleRepository.findAll();
        assertThat(personRoleList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePersonRole() throws Exception {
        // Initialize the database
        personRoleRepository.saveAndFlush(personRole);
        int databaseSizeBeforeDelete = personRoleRepository.findAll().size();

        // Get the personRole
        restPersonRoleMockMvc.perform(delete("/api/person-roles/{id}", personRole.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<PersonRole> personRoleList = personRoleRepository.findAll();
        assertThat(personRoleList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PersonRole.class);
        PersonRole personRole1 = new PersonRole();
        personRole1.setId(1L);
        PersonRole personRole2 = new PersonRole();
        personRole2.setId(personRole1.getId());
        assertThat(personRole1).isEqualTo(personRole2);
        personRole2.setId(2L);
        assertThat(personRole1).isNotEqualTo(personRole2);
        personRole1.setId(null);
        assertThat(personRole1).isNotEqualTo(personRole2);
    }
}

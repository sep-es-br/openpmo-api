package br.gov.es.openpmo.service;

import br.gov.es.openpmo.dto.workpackmodel.TransversalContextDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalWorkpackParticipationDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.repository.TransversalRepository;
import br.gov.es.openpmo.service.workpack.TransversalProjectService;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_PROGRAM_CONTEXT_INVALID;
import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_WORKPACK_NOT_ELIGIBLE;
import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_WORKPACK_NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransversalProjectServiceTest {

  private static final Long PROGRAM_ID = 100L;
  private static final Long WORKPACK_ID = 200L;

  private TransversalRepository repository;
  private TransversalProjectService service;

  @Before
  public void setUp() {
    this.repository = mock(TransversalRepository.class);
    this.service = new TransversalProjectService(this.repository);
  }

  @Test
  public void shouldRejectParticipationWhenProgramIsNotTransversal() {
    when(this.repository.findTransversalProgramContext(PROGRAM_ID)).thenReturn(Optional.empty());

    assertException(TRANSVERSAL_PROGRAM_CONTEXT_INVALID, () -> this.service.includeWorkpack(PROGRAM_ID, WORKPACK_ID));

    verify(this.repository, never()).existsWorkpack(WORKPACK_ID);
    verify(this.repository, never()).includeWorkpack(PROGRAM_ID, WORKPACK_ID);
  }

  @Test
  public void shouldRejectNewParticipationForIneligibleWorkpack() {
    allowProgramContext();
    when(this.repository.existsWorkpack(WORKPACK_ID)).thenReturn(true);
    when(this.repository.hasDirectParticipation(PROGRAM_ID, WORKPACK_ID)).thenReturn(false);
    when(this.repository.isEligibleWorkpack(PROGRAM_ID, WORKPACK_ID)).thenReturn(false);

    assertException(TRANSVERSAL_WORKPACK_NOT_ELIGIBLE, () -> this.service.includeWorkpack(PROGRAM_ID, WORKPACK_ID));

    verify(this.repository, never()).includeWorkpack(PROGRAM_ID, WORKPACK_ID);
  }

  @Test
  public void shouldAllowAnExistingHistoricalParticipationToRemainIncluded() {
    allowProgramContext();
    when(this.repository.existsWorkpack(WORKPACK_ID)).thenReturn(true);
    when(this.repository.hasDirectParticipation(PROGRAM_ID, WORKPACK_ID)).thenReturn(true);
    when(this.repository.isEligibleWorkpack(PROGRAM_ID, WORKPACK_ID)).thenReturn(false);

    final TransversalWorkpackParticipationDto response = this.service.includeWorkpack(PROGRAM_ID, WORKPACK_ID);

    assertEquals(PROGRAM_ID, response.getIdTransversalProgram());
    assertEquals(WORKPACK_ID, response.getIdWorkpack());
    org.junit.Assert.assertTrue(response.isIncluded());
    verify(this.repository).includeWorkpack(PROGRAM_ID, WORKPACK_ID);
  }

  @Test
  public void shouldUseIdempotentRepositoryInclusionForEligibleWorkpack() {
    allowProgramContext();
    when(this.repository.existsWorkpack(WORKPACK_ID)).thenReturn(true);
    when(this.repository.hasDirectParticipation(PROGRAM_ID, WORKPACK_ID)).thenReturn(false);
    when(this.repository.isEligibleWorkpack(PROGRAM_ID, WORKPACK_ID)).thenReturn(true);

    final TransversalWorkpackParticipationDto response = this.service.includeWorkpack(PROGRAM_ID, WORKPACK_ID);

    org.junit.Assert.assertTrue(response.isIncluded());
    verify(this.repository).includeWorkpack(PROGRAM_ID, WORKPACK_ID);
  }

  @Test
  public void shouldAllowManualRemovalWithoutCheckingCurrentEligibility() {
    allowProgramContext();
    when(this.repository.existsWorkpack(WORKPACK_ID)).thenReturn(true);

    final TransversalWorkpackParticipationDto response = this.service.removeWorkpack(PROGRAM_ID, WORKPACK_ID);

    assertEquals(PROGRAM_ID, response.getIdTransversalProgram());
    assertEquals(WORKPACK_ID, response.getIdWorkpack());
    org.junit.Assert.assertFalse(response.isIncluded());
    verify(this.repository).removeWorkpack(PROGRAM_ID, WORKPACK_ID);
    verify(this.repository, never()).isEligibleWorkpack(PROGRAM_ID, WORKPACK_ID);
  }

  @Test
  public void shouldUseProjectRepositoryForProjectInclusion() {
    allowProgramContext();
    when(this.repository.existsProject(WORKPACK_ID)).thenReturn(true);
    when(this.repository.hasDirectParticipation(PROGRAM_ID, WORKPACK_ID)).thenReturn(false);
    when(this.repository.isEligibleWorkpack(PROGRAM_ID, WORKPACK_ID)).thenReturn(true);

    final var response = this.service.includeProject(PROGRAM_ID, WORKPACK_ID);

    org.junit.Assert.assertTrue(response.isIncluded());
    verify(this.repository).includeProject(PROGRAM_ID, WORKPACK_ID);
    verify(this.repository, never()).includeWorkpack(PROGRAM_ID, WORKPACK_ID);
  }

  @Test
  public void shouldUseProjectRepositoryForProjectRemoval() {
    allowProgramContext();
    when(this.repository.existsProject(WORKPACK_ID)).thenReturn(true);

    final var response = this.service.removeProject(PROGRAM_ID, WORKPACK_ID);

    org.junit.Assert.assertFalse(response.isIncluded());
    verify(this.repository).removeProject(PROGRAM_ID, WORKPACK_ID);
    verify(this.repository, never()).removeWorkpack(PROGRAM_ID, WORKPACK_ID);
    verify(this.repository, never()).isEligibleWorkpack(PROGRAM_ID, WORKPACK_ID);
  }

  @Test
  public void shouldRejectMissingWorkpackBeforeCheckingEligibility() {
    allowProgramContext();
    when(this.repository.existsWorkpack(WORKPACK_ID)).thenReturn(false);

    assertException(TRANSVERSAL_WORKPACK_NOT_FOUND, () -> this.service.includeWorkpack(PROGRAM_ID, WORKPACK_ID));

    verify(this.repository, never()).isEligibleWorkpack(PROGRAM_ID, WORKPACK_ID);
    verify(this.repository, never()).includeWorkpack(PROGRAM_ID, WORKPACK_ID);
  }

  private void allowProgramContext() {
    when(this.repository.findTransversalProgramContext(PROGRAM_ID))
      .thenReturn(Optional.of(new TransversalContextDto()));
  }

  private void assertException(final String expectedMessage, final Runnable action) {
    try {
      action.run();
      fail("Expected NegocioException with message " + expectedMessage);
    } catch (NegocioException exception) {
      assertEquals(expectedMessage, exception.getMessage());
    }
  }
}

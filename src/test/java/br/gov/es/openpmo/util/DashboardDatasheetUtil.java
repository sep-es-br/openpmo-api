package br.gov.es.openpmo.util;

import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.stereotype.Component;

@Component
public class DashboardDatasheetUtil {

  WorkpackRepository workpackRepository;

  public DashboardDatasheetUtil(final WorkpackRepository workpackRepository) {
    this.workpackRepository = workpackRepository;
  }

  public Workpack createWorkpack() {
    final Workpack workpack = new Workpack();
    return this.workpackRepository.save(workpack);
  }

  public void createProjectRelationships(final Workpack workpack) {
    final Project project1 = this.createProject();
    final Project project2 = this.createProject();
    final Project project3 = this.createProject();
    final Project project4 = this.createProject();
    final Project project5 = this.createProject();

    this.link(project1, workpack);
    this.link(project2, workpack);
    this.link(project3, workpack);
    this.link(project4, workpack);
    this.link(project5, workpack);

    final Project project6 = this.createProject();
    final Project project7 = this.createProject();
    final Project project8 = this.createProject();
    final Project project9 = this.createProject();
    final Project project10 = this.createProject();

    this.link(project6, project1);
    this.link(project7, project1);
    this.link(project8, project1);
    this.link(project9, project1);
    this.link(project10, project1);
  }

  public Project createProject() {
    final Project workpack = new Project();
    return this.workpackRepository.save(workpack);
  }

  public void link(final Workpack child, final Workpack parent) {
    this.workpackRepository.createIsInRelationship(child.getId(), parent.getId());
  }

  public void createMilestoneRelationships(final Workpack workpack) {
    final Milestone milestone1 = this.createMilestone();
    final Milestone milestone2 = this.createMilestone();
    final Milestone milestone3 = this.createMilestone();
    final Milestone milestone4 = this.createMilestone();
    final Milestone milestone5 = this.createMilestone();

    this.link(milestone1, workpack);
    this.link(milestone2, workpack);
    this.link(milestone3, workpack);
    this.link(milestone4, workpack);
    this.link(milestone5, workpack);

    final Milestone milestone6 = this.createMilestone();
    final Milestone milestone7 = this.createMilestone();
    final Milestone milestone8 = this.createMilestone();
    final Milestone milestone9 = this.createMilestone();
    final Milestone milestone10 = this.createMilestone();

    this.link(milestone6, milestone1);
    this.link(milestone7, milestone1);
    this.link(milestone8, milestone1);
    this.link(milestone9, milestone1);
    this.link(milestone10, milestone1);
  }

  public Milestone createMilestone() {
    final Milestone milestone = new Milestone();
    return this.workpackRepository.save(milestone);
  }

  public void createDeliverablesRelationships(final Workpack workpack) {
    final Deliverable deliverable1 = this.createDeliverable();
    final Deliverable deliverable2 = this.createDeliverable();
    final Deliverable deliverable3 = this.createDeliverable();
    final Deliverable deliverable4 = this.createDeliverable();
    final Deliverable deliverable5 = this.createDeliverable();

    this.link(deliverable1, workpack);
    this.link(deliverable2, workpack);
    this.link(deliverable3, workpack);
    this.link(deliverable4, workpack);
    this.link(deliverable5, workpack);

    final Deliverable deliverable6 = this.createDeliverable();
    final Deliverable deliverable7 = this.createDeliverable();
    final Deliverable deliverable8 = this.createDeliverable();
    final Deliverable deliverable9 = this.createDeliverable();
    final Deliverable deliverable10 = this.createDeliverable();

    this.link(deliverable6, deliverable1);
    this.link(deliverable7, deliverable1);
    this.link(deliverable8, deliverable1);
    this.link(deliverable9, deliverable1);
    this.link(deliverable10, deliverable1);
  }

  public Deliverable createDeliverable() {
    final Deliverable deliverable = new Deliverable();
    return this.workpackRepository.save(deliverable);
  }

}
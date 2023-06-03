package bean;

import ENUM.StatusProject;
import dto.Keyword;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@RequestScoped
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    dao.User userDao;
    @EJB
    dao.Token tokenDao;
    @EJB
    dao.Project projDao;
    @EJB
    dao.Keyword keywordDao;


    public Project(){
    }

    public dto.Project convertProjEntityToDto(entity.Project p){

        dto.Project projDto = new dto.Project();

        projDto.setId(p.getId());
        projDto.setTitle(p.getTitle());
        projDto.setOffice(p.getOffice());
        projDto.setDetails(p.getDetails());
        projDto.setResources(p.getResources());
        projDto.setStatus(p.getStatus());
        projDto.setMembersNumber(p.getMembersNumber());
        projDto.setCreationDate(p.getCreationDate());

return projDto;
    }


    public boolean createNewProject (dto.Project project, String token){

        boolean res= false;

        if(project!=null && !projInfoIsFilledIn(project)){

            entity.Project newProjEnt = new entity.Project();
            newProjEnt.setCreationDate(Date.from(Instant.now()));
            newProjEnt.setStatus(StatusProject.PLANNING);
            newProjEnt.setTitle(project.getTitle());
            newProjEnt.setDetails(project.getDetails());

            if(project.getOffice()!=null){
                newProjEnt.setOffice(project.getOffice());
            }

            if(project.getResources()!= null){
                newProjEnt.setResources(project.getResources());
            }

            if(project.getMembersNumber()!=0){
                // TODO no frontend colocar 0 se não houver input ?!
                newProjEnt.setMembersNumber(project.getMembersNumber());
            } else {
                newProjEnt.setMembersNumber(4);
            }

            projDao.persist(newProjEnt);
            System.out.println(newProjEnt.getId());

            //TODO persist keywords connection to project
            associateKeywordsWithProject(project.getKeywords(), newProjEnt);




            res=true;

        }

        return res;
    }

    private void associateKeywordsWithProject(List<Keyword> keywords, entity.Project newProjEnt) {
        // associar as keywords ao projecto. Se já existir na DB basta adicionar a relação senão é preciso criar a keyword e adicionar à DB
        // se encontrar keyword entity pelo title, apenas associa ao proj.

        for (Keyword k: keywords) {
            entity.Keyword keyw = keywordDao.findKeywordByTitle(k.getTitle());

            if (keyw!= null){
                // já existe na DB, basta associar ao proj ---- adicionar a cada uma das listas ?!
                keyw.getListProject_Keywords().add(newProjEnt);
                newProjEnt.getListKeywords().add(keyw);

                projDao.merge(newProjEnt);
                keywordDao.merge(keyw);

            } else {
                // não existe keyword para o title usado. É necessário criar e adicionar à DB

                entity.Keyword newKeyW = new entity.Keyword();
                newKeyW.setTitle(k.getTitle());
                newKeyW.getListProject_Keywords().add(newProjEnt);

                keywordDao.persist(newKeyW);
                newProjEnt.getListKeywords().add(newKeyW);
                projDao.merge(newProjEnt);
            }
        }


    }

    private boolean projInfoIsFilledIn(dto.Project project) {
        // registo inicial do proj tem de incluir, no mínimo nome e descrição e ainda 1 keyword associada ao proj
        boolean res= false;

        if(checkStringInfo(project.getTitle()) || checkStringInfo(project.getDetails()) || project.getKeywords().isEmpty()){
            res=true;
            // projecto não inclui info indispensável no momento da sua criação
        }

        return res;
    }

    private boolean checkStringInfo(String str) {
        // check if a string info is null or blank
        boolean res = false;

        if(str == null || str.isBlank()){
            res=true;
            // info is not filled in as it should
        }

        return res;
    }

}

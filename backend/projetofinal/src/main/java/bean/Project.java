package bean;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;

import java.io.Serializable;

@RequestScoped
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    dao.User userDao;
    @EJB
    dao.Token tokenDao;


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

}

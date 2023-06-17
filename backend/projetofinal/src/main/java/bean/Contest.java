package bean;

import jakarta.enterprise.context.RequestScoped;
import org.jboss.logging.Logger;

@RequestScoped
public class Contest {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(User.class);


    public Contest(){
    }


    public boolean createNewContest(dto.Contest contest){
        boolean res = false;

        //TODO verificar se é gestor de concursos no endpoint





        return res;
    }


}

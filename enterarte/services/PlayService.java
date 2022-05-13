package com.enterarte.services;

import com.enterarte.entities.Location;
import com.enterarte.entities.Photo;
import com.enterarte.entities.Play;
import com.enterarte.mistakes.ErrorService;
import com.enterarte.repositories.PlayRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PlayService {
    
    @Autowired
    private PhotoService photoService;

//    @Autowired
//    public final NotificationService notificacionService;
    public final PlayRepository playRepository;
    
    @Autowired
    public PlayService(PlayRepository playRepository) {
        this.playRepository = playRepository;
    }
    
    @Transactional(rollbackOn = Exception.class)
    public void save(Play play, Location location, Optional<MultipartFile> file) throws Exception {
        
//        validar(play);        
       
        if (file.isPresent() && !file.get().isEmpty()) {
            Photo photo = photoService.guardarFoto(file.get());
            play.setPhoto(photo);
        }
        
        play.setLocation(location);
        play.setAlta(true);
        
        playRepository.save(play);
    }
    
    public List<Play> listarPlay() {        
        playRepository.findAll();
        return playRepository.findAll();
    }
    
    public List<Play> listarPlaysActivas() {
        
        return playRepository.playActivos(Boolean.TRUE);
    }
    
    public List<Play> listarPlaysBajas() {
        
        return playRepository.playActivos(Boolean.FALSE);
    }
    
    @Transactional
    public Play findById(String id) throws Exception {
        Optional<Play> option = playRepository.findById(id);
        if (option.isPresent()) {
            Play play = option.get();
            return play;
        } else {
            throw new Exception("usuario no encontrado");
        }
    }
    
    public void baja(Play play){
        play.setAlta(false);
        playRepository.save(play);
    }

     public void alta(Play play){
        play.setAlta(true);
        playRepository.save(play);
    }
///////////////////////////////Validaciones/////////////////////////////////////
    public void validar(Play play) throws ErrorService {
//        validaSiExiste(play);
        validaNombre(play);
        validaDuracion(play);
        validaDescripcion(play);
        
    }
    
    private void validaSiExiste(Play play) throws ErrorService {
        
        Optional<Play> optionalplay = null;
        optionalplay = playRepository.findByName(play.getNombre());
        
        if (optionalplay.isPresent()) {
            System.out.println(optionalplay.get());
            throw new ErrorService("Ya existe una obra con ese nombre");            
        }
    }
    
    private void validaNombre(Play play) throws ErrorService {
        
        if (play.getNombre().isEmpty()) {
            throw new ErrorService("Tiene que ingresar un nombre");
        }
        
        Pattern pattern = Pattern
                .compile("^[a-zA-Z\\s]+{3,25}");
        Matcher mather = pattern.matcher(play.getNombre());
        if (mather.find() != true) {
            throw new ErrorService("caracteres invalido nombre.");
        }
        
    }
    
    private void validaDescripcion(Play play) throws ErrorService {
        
        if (play.getDescripcion().isEmpty()) {
            throw new ErrorService("Tiene que ingresar una descripcion");
        }
        if (play.getDescripcion().length() < 3 || play.getDescripcion().length() > 999) {
            throw new ErrorService("largo de caracteres invalido");
        }
    }
    
    private void validaDuracion(Play play) throws ErrorService {
        
        if (play.getDuracion() == 0) {
            throw new ErrorService("Tiene que ingresar una duracion");
        }
        
        if (play.getDuracion() < 10 || play.getDuracion() > 180) {
            throw new ErrorService("La duracion debe ser entre 10 a 180 minutos");
            
        }
        
        Pattern pattern = Pattern
                .compile("^[0123456789]{2,3}$");
        Matcher mather = pattern.matcher(play.getDuracion().toString());
        if (mather.find() != true) {
            throw new ErrorService("La duracion solo permite caracter numericos");
        }
        
    }
    
}

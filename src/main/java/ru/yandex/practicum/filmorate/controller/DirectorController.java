package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping ("directors")
public class DirectorController {
    private final DirectorService directorService;

   @GetMapping
   public ResponseEntity<List<Director>> getAll () {
       List<Director> directorList = directorService.getAll();
       return new ResponseEntity<>(directorList, HttpStatus.OK);
   }

   @GetMapping ("{id}")
   public ResponseEntity<Director> getById (@PathVariable Integer id) {
       Director director = directorService.getById(id);
       return new ResponseEntity<>(director, HttpStatus.OK);
   }



   @PostMapping
   public ResponseEntity<Director> create (@RequestBody Director director) {
       Director createdDirector = directorService.create(director);
       return new ResponseEntity<>(createdDirector, HttpStatus.OK);
   }


    @PutMapping
    public ResponseEntity<Director> update (@RequestBody Director director) {
        Director updatedDirector = directorService.update(director);
        return new ResponseEntity<>(updatedDirector, HttpStatus.OK);
    }

    @DeleteMapping ("{id}")
    public ResponseEntity<Director> delete (@PathVariable Integer id) {
        Director director = directorService.delete(id);
        return new ResponseEntity<>(director, HttpStatus.OK);
    }
}

package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.dtos.PointOfInterestCreateDto;
import cloudflight.integra.backend.model.dtos.PointOfInterestDto;
import cloudflight.integra.backend.service.PointOfInterestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pois")
public class PointOfInterestController {
    private final PointOfInterestService pointOfInterestService;

    @Autowired
    public PointOfInterestController(PointOfInterestService pointOfInterestService) {
        this.pointOfInterestService = pointOfInterestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PointOfInterestDto savePointOfInterest(@Valid @RequestBody PointOfInterestCreateDto pointOfInterestCreateDto) {
        return pointOfInterestService.save(pointOfInterestCreateDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PointOfInterestDto> getAll() {
        return pointOfInterestService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PointOfInterestDto findById(@PathVariable Long id) {
        return pointOfInterestService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PointOfInterestDto update(@PathVariable Long id, @Valid @RequestBody PointOfInterestCreateDto pointOfInterestCreateDto) {
        return pointOfInterestService.update(id, pointOfInterestCreateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        pointOfInterestService.deleteById(id);
    }

}

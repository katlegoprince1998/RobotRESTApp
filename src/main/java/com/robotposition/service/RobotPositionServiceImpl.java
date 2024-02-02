package com.robotposition.service;

import com.robotposition.data.payload.request.CreateRobotPositionRequest;
import com.robotposition.data.payload.request.UpdateRobotPositionRequest;
import com.robotposition.exception.DuplicateRobotPositionException;
import com.robotposition.helper.RobotCommandsHelper;
import com.robotposition.model.RobotPosition;
import com.robotposition.repository.RobotPositionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Hari
 * Service layer methods invoked by the controller layer.
 * These service methods invoke the data layer (repository) to perform the CRUD operations.
 */
@Service
public class RobotPositionServiceImpl implements IRobotPositionService {

    private final RobotPositionRepository robotPositionRepository;

    private final RobotCommandsHelper robotCommandsHelper;

    public RobotPositionServiceImpl(final RobotPositionRepository robotPositionRepository, final RobotCommandsHelper robotCommandsHelper) {
        this.robotPositionRepository = robotPositionRepository;
        this.robotCommandsHelper = robotCommandsHelper;
    }

    /**
     * @param robotPositionRequest CreateRobotPositionRequest
     * @return RobotPosition
     */

    @Override
    public RobotPosition createRobotPosition(final CreateRobotPositionRequest robotPositionRequest) {
        try {
            final RobotPosition robotPosition = RobotPosition.builder()
                    .xpos(robotPositionRequest.getXpos())
                    .ypos(robotPositionRequest.getYpos())
                    .facingdir(robotPositionRequest.getFacingdir())
                    .build();
            return robotPositionRepository.save(robotPosition);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateRobotPositionException(e);
        }
    }

    @Override
    public Optional<RobotPosition> findRobotPositionById(final Integer id) {
        return robotPositionRepository.findById(id);
    }

    /**
     * @param request UpdateRobotPositionRequest
     * @return RobotPosition
     */
    @Override
    public RobotPosition updateRobotPosition(final UpdateRobotPositionRequest request) {

        try {
            final Optional<RobotPosition> currentRobotPosition = robotPositionRepository.findById(request.getRobotPositionId());
            if (currentRobotPosition.isPresent()) {
                RobotPosition updatedRobotPosition = robotCommandsHelper.updateRobotPositionBasedOnCommands(currentRobotPosition.get(), request.getRobotPositionCommands());
                updatedRobotPosition = robotPositionRepository.save(updatedRobotPosition);
                return updatedRobotPosition;
            }

        } catch (DataIntegrityViolationException e) {
            throw new DuplicateRobotPositionException(e);
        }
        return new RobotPosition();
    }

    @Override
    public void deleteRobotPositionById(final Integer id) {
        robotPositionRepository.deleteById(id);
    }

    @Override
    public List<RobotPosition> findAllRobotPositions() {
        return robotPositionRepository.findAll();
    }


    @Override
    public Page<RobotPosition> pagination(int offset, int pageSize, String field) {

        if ("defaultValue".equals(field)) {
            return robotPositionRepository.findAll(
                    PageRequest.of(offset, pageSize)
            );
        }
        return robotPositionRepository.findAll(
                PageRequest.of(offset, pageSize).withSort(Sort.by(field))
        );
    }
}

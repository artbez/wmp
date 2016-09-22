package com.qreal.wmp.db.robot.server;

import com.qreal.wmp.db.robot.dao.RobotDao;
import com.qreal.wmp.db.robot.exceptions.Aborted;
import com.qreal.wmp.db.robot.exceptions.ErrorConnection;
import com.qreal.wmp.db.robot.exceptions.NotFound;
import com.qreal.wmp.db.robot.model.robot.RobotSerial;
import com.qreal.wmp.thrift.gen.*;
import org.springframework.context.ApplicationContext;

/** Thrift server side handler for RobotDBService.*/
public class RobotDbServiceHandler implements RobotDbService.Iface {

    private RobotDao robotDao;

    public RobotDbServiceHandler(ApplicationContext context) {
        robotDao = (RobotDao) context.getBean("robotDao");
        assert robotDao != null;
    }

    @Override
    public long registerRobot(TRobot tRobot) throws TAborted, TIdAlreadyDefined {
        long id = 0;
        if (tRobot.isSetId()) {
            throw new TIdAlreadyDefined("Robot id not null. To saveRobot robot you should not assign id to robot.");
        }
        try {
            id = robotDao.saveRobot(new RobotSerial(tRobot));
        } catch (Aborted e) {
            throw new TAborted(e.getTextCause(), e.getMessage(), e.getFullClassName());
        }
        return id;
    }

    @Override
    public TRobot findById(long robotId) throws TNotFound {
        RobotSerial robot = null;
        try {
            robot = robotDao.getRobot(robotId);
        } catch (NotFound e) {
            throw new TNotFound(String.valueOf(robotId), "Robot not found.");
        }
        return robot.toTRobot();
    }

    @Override
    public void deleteRobot(long robotId) throws TAborted, TErrorConnection {
        try {
            robotDao.deleteRobot(robotId);
        } catch (ErrorConnection e) {
            throw new TErrorConnection(e.getNameClient(), e.getMessage());
        } catch (Aborted e) {
            throw new TAborted(e.getTextCause(), e.getMessage(), e.getFullClassName());
        }
    }

    @Override
    public boolean isRobotExists(long id) {
        return robotDao.isExistsRobot(id);
    }

    @Override
    public void updateRobot(TRobot tRobot) throws TAborted, TIdNotDefined {
        if (!tRobot.isSetId()) {
            throw new TIdNotDefined("Robot id is null. To rewrite robot you should specify id.");
        }
        try {
            robotDao.updateRobot(new RobotSerial(tRobot));
        } catch (Aborted e) {
            throw new TAborted(e.getTextCause(), e.getMessage(), e.getFullClassName());
        }
    }

    /** Only for sake of testing. */
    public RobotDao getRobotDao() {
        return robotDao;
    }

    /** Only for sake of testing. */
    public void setRobotDao(RobotDao robotDao) {
        this.robotDao = robotDao;
    }

    /** Only for sake of testing. */
    public void rewindRobotDao() {
        this.robotDao = null;
    }
}

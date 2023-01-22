package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.z3db0y.flagship.DriveTrain;
import com.z3db0y.flagship.Logger;

import org.firstinspires.ftc.teamcode.Enums;
import org.firstinspires.ftc.teamcode.Flags;
import org.firstinspires.ftc.teamcode.TickUtils;
import org.firstinspires.ftc.teamcode.autonomous.AutonomousOpMode;

@Flags(robotType = Enums.RobotType.H_DRIVE, alliance = Enums.Alliance.BLUE, startingPosition = Enums.StartPosition.RIGHT)
@Autonomous(name="Right", group="FTC22_Autonomous")
public class Right extends AutonomousOpMode {
    @Override
    public void run() {
        rotatingBaseHelper.runToAsync(0, 1);
        Logger.setTelemetry(telemetry);
        super.run();
        driveTrain.strafeCM(17, 1, DriveTrain.Direction.LEFT);
        Logger.addData("Strafed 17 cm");
        Logger.update();
        driveTrain.turn(90, 1, imu);
        Logger.addData("Turned 90 degrees");
        Logger.update();
        driveTrain.driveCM(110, 1, DriveTrain.Direction.FORWARD);
        Logger.addData("Drove 110 cm");
        Logger.update();
        driveTrain.strafeCM(29, 1, DriveTrain.Direction.RIGHT);
        Logger.addData("Strafed 29 cm");
        Logger.update();
        driveTrain.driveCM(5, 1, DriveTrain.Direction.BACKWARD);
        Logger.addData("Drove 5 cm back");
        Logger.update();
        rotatingBaseServo.setPosition(1);
        extension.runToPosition(-TickUtils.cmToTicks(15, 288, 4.75/2), 0.7);
        Logger.addData("Extended 15 cm");
        Logger.update();
        Logger.addData("Released rotating base");
        Logger.update();
        sleep(350);
        rotatingBaseHelper.runTo(90, 1);
        Logger.addData("Rotated base 90 degrees");
        Logger.update();
        slideMotors.runToPosition(TickUtils.cmToTicks(15, 28 * 15, 4.75/2), 1);
        Logger.addData("Lifted slide 15 cm");
        Logger.update();
        openClaw();
        sleep(500);
        Logger.addData("Opened claw");
        Logger.update();
        extension.runToPosition(-TickUtils.cmToTicks(22, 288, 4.75/2), 0.7);
        Logger.addData("Extended 22 cm");
        Logger.update();
        closeClaw();
        sleep(500);
        Logger.addData("Closed claw");
        Logger.update();
        slideMotors.runToPosition(TickUtils.cmToTicks(20, 45 * 15, 4.75/2), 1);
        Logger.addData("Lifted slide 20 cm");
        Logger.update();
        rotatingBaseHelper.runTo(0, 0.25);
        Logger.addData("Rotated base 0 degrees");
        Logger.update();
        extension.runToPosition(TickUtils.cmToTicks(30, 288, 4.75/2), 0.7);
        Logger.addData("Retracted 30 cm");
        Logger.update();
        slideMotors.runToPosition(-TickUtils.cmToTicks(45, 28 * 15, 4.75/2), 0.6);
        Logger.addData("Lowered slide 45 cm");
        Logger.update();
        openClaw();
        sleep(500);
        Logger.addData("Opened claw");
        Logger.update();
        slideMotors.runToPosition(TickUtils.cmToTicks(20, 28 * 15, 4.75/2), 0.6);
        Logger.addData("Lifted slide 20 cm");
        Logger.update();
        rotatingBaseHelper.runTo(90, 0.25);
        Logger.addData("Rotated base 90 degrees");
        Logger.update();
        extension.runToPosition(-TickUtils.cmToTicks(30, 288, 4.75/2), 0.7);
        Logger.addData("Extended 30 cm");
        Logger.update();
        slideMotors.runToPosition(-TickUtils.cmToTicks(10, 28 * 15, 4.75/2), 0.6);
        Logger.addData("Lowered slide 10 cm");
        Logger.update();
        closeClaw();
        sleep(500);
        Logger.addData("Closed claw");
        Logger.update();
//        extension.runToPosition(TickUtils.cmToTicks(25, 288, 4.75/2), 0.7);
//        Logger.addData("Retracted 25 cm");
//        Logger.update();
//        driveTrain.driveCM(5, 1, DriveTrain.Direction.FORWARD);
//        Logger.addData("Drove 5 cm");
//        Logger.update();
//        driveTrain.strafeCM(40, 1, DriveTrain.Direction.LEFT);
//        Logger.addData("Strafed 40 cm");
//        Logger.update();
//        rotatingBaseHelper.runTo(-45, 0.35);
//        Logger.addData("Rotated base -45 degrees");
//        Logger.update();
//        slideMotors.runToPosition(TickUtils.cmToTicks(40, 45 * 15, 4.75/2), 1);
//        Logger.addData("Lifted slide 40 cm");
//        Logger.update();
//        sleep(1500);
//        Logger.addData("Waited 1.5 seconds");
//        Logger.update();
//        extension.runToPosition(-TickUtils.cmToTicks(10, 288, 4.75/2), 0.7);
//        Logger.addData("Extended 10 cm");
//        Logger.update();
//        slideMotors.runToPosition(-TickUtils.cmToTicks(20, 45 * 15, 4.75/2), 0.5);
//        Logger.addData("Lowered slide 20 cm");
//        Logger.update();
//        openClaw();
//        while(!Thread.currentThread().isInterrupted()) {}
    }
}

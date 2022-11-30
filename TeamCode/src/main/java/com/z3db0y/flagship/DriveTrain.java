package com.z3db0y.flagship;


import android.util.Log;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DriveTrain {
    public MotorWithLocation[] motors;
    Type type;
    double ticksPerRevolution;
    double wheelDiameter;
    double gearRatio;

    public enum Direction {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    }

    public enum Type {
        MECANUM(new int[]{4}, new MotorWithLocation.Location[]{
                MotorWithLocation.Location.FRONT_LEFT,
                MotorWithLocation.Location.FRONT_RIGHT,
                MotorWithLocation.Location.BACK_LEFT,
                MotorWithLocation.Location.BACK_RIGHT
        }),
        TANK(new int[]{2, 4, 6, 8}, new MotorWithLocation.Location[]{
                MotorWithLocation.Location.LEFT,
                MotorWithLocation.Location.RIGHT
        }),
        X_DRIVE(new int[]{4}, new MotorWithLocation.Location[]{
                MotorWithLocation.Location.LEFT,
                MotorWithLocation.Location.RIGHT,
                MotorWithLocation.Location.FRONT,
                MotorWithLocation.Location.BACK
        });

        public final int[] validMotorCounts;
        public final MotorWithLocation.Location[] motorLocations;

        Type(int[] validMotorCounts, MotorWithLocation.Location[] validLocations) {
            this.validMotorCounts = validMotorCounts;
            this.motorLocations = validLocations;
        }
    }

    public static class MotorWithLocation extends Motor {
        public Location location;

        public enum Location {
            FRONT_LEFT,
            FRONT_RIGHT,
            BACK_LEFT,
            BACK_RIGHT,
            LEFT,
            RIGHT,
            FRONT,
            BACK;
        }

        public MotorWithLocation(DcMotorImplEx motor, Location location) {
            super(motor);
            this.location = location;
        }
    }

    public DriveTrain(@NonNull Type type, @NonNull MotorWithLocation[] motors, double wheelDiameter, double gearRatio) {
        this.type = type;
        this.motors = motors;
        this.wheelDiameter = wheelDiameter;
        this.gearRatio = gearRatio;
        this.ticksPerRevolution = 28 * gearRatio;

        for (int num : type.validMotorCounts) {
            if (motors.length == num) {
                for (MotorWithLocation motor : motors) {
                    if (!Arrays.asList(type.motorLocations).contains(motor.location)) {
                        throw new IllegalArgumentException("Invalid motor location");
                    }
                    motor.getMotorType().setTicksPerRev(ticksPerRevolution);
                }
                return;
            }
        }
        throw new IllegalArgumentException("Invalid motor locations for drive train type");
    }

    public void runTicks(int left, int right, double velocity) {
        for (MotorWithLocation motor : this.motors) {
            switch (motor.location) {
                case LEFT:
                case BACK_LEFT:
                case FRONT_LEFT:
                    motor.setRelativeTargetPosition(left);
                    break;
                case RIGHT:
                case BACK_RIGHT:
                case FRONT_RIGHT:
                    motor.setRelativeTargetPosition(right);
                    break;
            }
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setVelocity(motor.getMotorType().getAchieveableMaxTicksPerSecond() * velocity);
        }
    }

    public boolean isBusy() {
        for (MotorWithLocation motor : this.motors) {
            if (motor.isBusy()) {
                int ticks = motor.getCurrentPosition();
                Log.i("DriveTrain", "Motor " + motor.location + " is busy at " + ticks + " ticks");
                return true;
            }
        }
        return false;
    }

    public void driveRobotCentric(double forwardVelo, double turnVelo, double strafeVelo) {
        for (MotorWithLocation motor : this.motors) {
            double resultantVelo = 0;
            switch (motor.location) {
                case FRONT_LEFT:
                    resultantVelo = forwardVelo - turnVelo - strafeVelo;
                    break;
                case FRONT_RIGHT:
                    resultantVelo = forwardVelo + turnVelo + strafeVelo;
                    break;
                case BACK_LEFT:
                    resultantVelo = forwardVelo - turnVelo + strafeVelo;
                    break;
                case BACK_RIGHT:
                    resultantVelo = forwardVelo + turnVelo - strafeVelo;
                    break;
                case LEFT:
                    resultantVelo = forwardVelo - turnVelo;
                    break;
                case RIGHT:
                    resultantVelo = forwardVelo + turnVelo;
                    break;
                case FRONT:
                    resultantVelo = -strafeVelo - turnVelo;
                    break;
                case BACK:
                    resultantVelo = -strafeVelo + turnVelo;
                    break;
            }
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setVelocity(motor.getMotorType().getAchieveableMaxTicksPerSecond() * resultantVelo);
        }
    }

    public void driveFieldCentric(double forwardVelo, double turnVelo, double strafeVelo, double angle) {
        double[] velocities = new double[4];
        velocities[0] = forwardVelo - turnVelo - strafeVelo;
        velocities[1] = forwardVelo + turnVelo + strafeVelo;
        velocities[2] = forwardVelo - turnVelo + strafeVelo;
        velocities[3] = forwardVelo + turnVelo - strafeVelo;

        double max = Arrays.stream(velocities).max().getAsDouble();
        double min = Arrays.stream(velocities).min().getAsDouble();

        if (max > 1 || min < -1) {
            double scale = 1 / Math.max(Math.abs(max), Math.abs(min));
            for (int i = 0; i < velocities.length; i++) {
                velocities[i] *= scale;
            }
        }

        for (int i = 0; i < velocities.length; i++) {
            velocities[i] *= Math.cos(angle);
        }

        for (MotorWithLocation motor : this.motors) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setVelocity(motor.getMotorType().getAchieveableMaxTicksPerSecond() * velocities[motor.location.ordinal()]);
        }
    }

    // KEEP THE SAME HADING ANGLE
    public void drive(int ticks, double velocity, Direction direction) {
        int left = 0;
        int right = 0;
        switch (direction) {
            case FORWARD:
                left = ticks;
                right = ticks;
                break;
            case BACKWARD:
                left = -ticks;
                right = -ticks;
                break;
        }
        runTicks(left, right, velocity);
    }

    public void driveCM(double cm, double velocity, Direction direction) {
        double wheelCirc = Math.PI * wheelDiameter;
        int ticks = (int) (cm / wheelCirc * ticksPerRevolution);
        Log.i("DriveTrain", "Ticks: " + ticks);
        drive(ticks, velocity, direction);
    }

    // Todo: keep the same heading
    public void strafe(int ticks, double velocity, Direction direction) {
        switch (direction){
            case LEFT:
                for (MotorWithLocation motor : this.motors) {
                    switch (motor.location) {
                        case FRONT_LEFT:
                            motor.setRelativeTargetPosition(-ticks);
                        case BACK_LEFT:
                            motor.setRelativeTargetPosition(ticks);
                            break;
                        case FRONT_RIGHT:
                            motor.setRelativeTargetPosition(ticks);
                            break;
                        case BACK_RIGHT:
                            motor.setRelativeTargetPosition(-ticks);
                            break;
                    }
                    motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    motor.setVelocity(motor.getMotorType().getAchieveableMaxTicksPerSecond() * velocity);
                }
                break;
            case RIGHT:
                for (MotorWithLocation motor : this.motors) {
                    switch (motor.location) {
                        case FRONT_LEFT:
                            motor.setRelativeTargetPosition(ticks);
                        case BACK_LEFT:
                            motor.setRelativeTargetPosition(-ticks);
                            break;
                        case FRONT_RIGHT:
                            motor.setRelativeTargetPosition(-ticks);
                            break;
                        case BACK_RIGHT:
                            motor.setRelativeTargetPosition(ticks);
                            break;
                    }
                    motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    motor.setVelocity(motor.getMotorType().getAchieveableMaxTicksPerSecond() * velocity);
                }
                break;
        }
    }

    public void strafeCM(double cm, double velocity, Direction direction) {
        int ticks = (int) (cm * (ticksPerRevolution / (wheelDiameter * Math.PI)) * gearRatio);
        strafe(ticks, velocity, direction);
    }

    public double normalizeAngle(double angle) {
        while (angle > 180) {
            angle -= 360;
        }
        while (angle < -180) {
            angle += 360;
        }
        return angle;
    }

    public void turn(double target, double velocity, double padding, BNO055IMU imu) {
        double current = imu.getAngularOrientation().firstAngle + 180;
        double diff = normalizeAngle(target - current);
        if (diff > 0) {
            while (diff > padding) {
                driveRobotCentric(0, velocity, 0);
                current = imu.getAngularOrientation().firstAngle + 180;
                diff = normalizeAngle(target - current);
            }
        } 
        else {
            while (diff < -padding) {
                driveRobotCentric(0, -velocity, 0);
                current = imu.getAngularOrientation().firstAngle + 180;
                diff = normalizeAngle(target - current);
            }
        }
        driveRobotCentric(0, 0, 0);
    }

    // TODO: maybe add a drive field centric method
}

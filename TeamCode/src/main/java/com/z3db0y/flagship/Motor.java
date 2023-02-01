package com.z3db0y.flagship;


import android.util.Log;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotorControllerEx;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Motor extends DcMotorImplEx {
	boolean holdPosition = false;
	boolean currentlyHoldingPosition = false;
	RunMode runMode = RunMode.RUN_WITHOUT_ENCODER;
	double power = 0.0;
	double velocity = 0.0;
	AngleUnit veloUnit = null;
	int targetPosition = 0;

	public Motor(DcMotorImplEx motor) {
		super(motor.getController(), motor.getPortNumber());
	}

	// TODO: methods
	void updateMotorState() {
		Log.i("RunMode", String.valueOf(this.runMode));
		Log.i("Power", String.valueOf(this.power));
		Log.i("Velocity", String.valueOf(this.velocity));
		if(this.holdPosition && Math.abs(this.power) < 0.2 && Math.abs(this.velocity) < 20) {
			if(this.runMode == RunMode.STOP_AND_RESET_ENCODER) super.setMode(RunMode.STOP_AND_RESET_ENCODER);
			if(!this.currentlyHoldingPosition) {
				currentlyHoldingPosition = true;
				super.setTargetPosition(super.getCurrentPosition());
				super.setMode(RunMode.RUN_TO_POSITION);
				super.setPower(1);
			}
		}
		else {
			this.currentlyHoldingPosition = false;
			if(super.getTargetPosition() != this.targetPosition) super.setTargetPosition(this.targetPosition);
			if(super.getMode() != this.runMode) super.setMode(this.runMode);
			if(this.velocity != 0) {
				int dir = this.direction == Direction.FORWARD ? 1 : -1;
				if(veloUnit == null) ((DcMotorControllerEx) super.controller).setMotorVelocity(super.getPortNumber(), dir * this.velocity);
				else ((DcMotorControllerEx) super.controller).setMotorVelocity(super.getPortNumber(), dir * this.velocity, this.veloUnit);
			}
			else {
				if(super.getPower() != this.power) super.setPower(this.power);
			}

		}
	}
	
	public void setHoldPosition(boolean holdPosition) {
		this.holdPosition = holdPosition;
		updateMotorState();
	}

	long lastStallCheck = 0;
	int lastTicks = 0;
	public boolean stalled(int maxTicksPerSec) {
		if(lastStallCheck == 0) {
			lastStallCheck = System.currentTimeMillis();
			lastTicks = super.getCurrentPosition();
			return false;
		}
		else {
			long timeElapsed = System.currentTimeMillis() - lastStallCheck;
			int maxElapsedTicks = (int) (maxTicksPerSec * this.getPower() * (int) timeElapsed / 1000);
			int movedTicks = super.getCurrentPosition() - lastTicks;
			double threshold = .5;
			int minExpectedTicks = (int) (maxElapsedTicks * threshold);
			lastStallCheck = System.currentTimeMillis();
			lastTicks = super.getCurrentPosition();

			return movedTicks < minExpectedTicks;
		}
	}

	@Override
	public void setMode(@NonNull RunMode runMode) {
		this.runMode = runMode;
		updateMotorState();
	}

	@Override
	public void setPower(double power) {
		this.velocity = 0;
		this.power = power;
		updateMotorState();
	}

	@Override
	public void setTargetPosition(int targetPosition) {
		this.targetPosition = targetPosition;
		updateMotorState();
	}

	public void setRelativeTargetPosition(int targetPosition) {
		this.targetPosition = super.getCurrentPosition() + targetPosition;
		updateMotorState();
	}

	@Override
	public void setVelocity(double angularRate) {
		this.power = 0;
		this.velocity = angularRate;
		this.veloUnit = null;
		updateMotorState();
	}

	@Override
	public void setVelocity(double angularRate, AngleUnit unit) {
		this.power = 0;
		this.velocity = angularRate;
		this.veloUnit = unit;
		updateMotorState();
	}

	public boolean getHoldPosition() {
		return this.holdPosition;
	}

	@Override
	public RunMode getMode() {
		return this.runMode;
	}

	@Override
	public double getPower() {
		return this.power;
	}

	@Override
	public int getTargetPosition() {
		return this.targetPosition;
	}

	public void runToPosition(int ticks, double power, boolean relative) {
		if(relative) this.setRelativeTargetPosition(ticks);
		else this.setTargetPosition(ticks);
		this.setMode(RunMode.RUN_TO_POSITION);
		this.setPower(power);
		while (this.isBusy()) {}
		this.setPower(0);
	}

	public void runToPosition(int ticks, double power) {
		this.runToPosition(ticks, power, true);
	}

	public void runToPositionAsync(int ticks, double power, boolean relative) {
		if(relative) this.setRelativeTargetPosition(ticks);
		else this.setTargetPosition(ticks);
		this.setMode(RunMode.RUN_TO_POSITION);
		this.setPower(power);
	}

	public void runToPositionAsync(int ticks, double power) {
		this.runToPositionAsync(ticks, power, true);
	}

	public boolean atTargetPosition(){
		return Math.abs(this.getCurrentPosition() - this.getTargetPosition()) < 10;
	}
}

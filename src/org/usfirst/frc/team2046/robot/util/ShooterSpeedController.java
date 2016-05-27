package org.usfirst.frc.team2046.robot.util;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ShooterSpeedController {

	private int period = 10;
	private double target = 0;
	private double oldTarget = 0;
	private double tolerance = 0;
	private boolean enable;

	private final Timer timer = new Timer();
	private final TimerTask task;
	private final Object sync = new Object();
	private double gain;

	/**
	 * Constructs a simple Bang-Bang Speed Controller
	 */
	public ShooterSpeedController() {
		task = new TimerTask() {
			@Override
			public void run() {
				synchronized (sync) {
					double pv = Math.abs(getProcessValue());
					double sp = Math.abs(target);
					setOutput(pv < sp ? Math.signum(target) : 0.0);
				}
			}
		};
	}

	/**
	 * Constructs a Take-Back-Half Speed Controller
	 * 
	 * @param gain - integral gain used for tuning
	 * @param maxTarget - maximum target speed at full on
	 */
	public ShooterSpeedController(final double kI, final double maxTarget) {
		this.gain = kI;
		
		task = new TimerTask() {
			double error = 0;
			double output = 0;
			double tbh = 0;

			@Override
			public void run() {
				synchronized (sync) {

					// reset to new target
					if (target != oldTarget) {
						error = target > oldTarget ? 1.0 : -1.0;
						tbh = 2.0 * target / maxTarget - 1.0;
						oldTarget = target;
					}
					
					// integrate error
					double error = target - getProcessValue();
					output += error * gain;

					// clamp output
					output = Math.max(Math.min(output, 1.0), -1.0);

					// monitor for zero error crossing
					if (Math.signum(error) != Math.signum(this.error)) {
						// split the difference
						tbh = output = 0.5 * (output + tbh);
						this.error = error;
					}

					setOutput(output);
				}
			}
		};
	}
	
	/**
	 * Sets the controller task execution rate.
	 * @param period the intervol (in milliseconds) between task execution rate
	 */
	public void setControllerPeriod(int period) {
		this.period = period;
	}
	
	/**
	 * Typically used for debugging the shooter.
	 * @param kI PID gain setting - 0.0 is default, 1.0 = 1.0 gain, etc.
	 */
	public void setGain(double kI) {
		synchronized(sync) {
			this.gain = kI;
		}
	}
	
	/**
	 * Sets the target speed for the Shooter's speed.
	 * @param target General target speed for the shooter.
	 * @param tolerance A lee-way for corrections, i.e. 3890RPM / 4000RPM = Could be a 200RPM tolerance
	 */
	public void setTarget(double target, double tolerance) {
		synchronized (sync) {
			this.target = target;
		}
		this.tolerance = tolerance;
		setEnable(true);
	}
	
	/**
	 * Stops the Shooter from spinning up.
	 */
	public void cancel() {
		setEnable(false);
	}
	
	/**
	 * Determines how close the shooter is to the target speed.
	 * @return True = the shooter is +/- the tolerance, False = the shooter is not up-to-speed.
	 */
	public boolean isOnTarget() {
		return Math.abs(target - getProcessValue()) < tolerance;
	}

	/**
	 * Enables the Shooter's speed controller
	 * @param enable True starts up the speed controller (runs the motor up-to-speed using a taskschedule), false stops the speed controller (kills the motor speed and the timer).
	 */
	public void setEnable(boolean enable) {
		if (enable && !this.enable) {
			timer.scheduleAtFixedRate(task, 0, period);
		} else if (!enable && this.enable) {
			timer.cancel();
			setOutput(0.0);
		}
		this.enable = enable;
	}

	public abstract double getProcessValue();

	public abstract void setOutput(double output);
}

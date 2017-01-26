package org.usfirst.frc.team2046.robot.util;

import org.usfirst.frc.team2046.robot.commands.DelayedCallback;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Scheduler;

public class TripleSolenoid {
	private DoubleSolenoid solenoidA;
	private Solenoid solenoidB;
	private Solenoid solenoidC;


	/**
	 * Method overloading. Declares a Triple solenoid using 3 pneumatic channels, where only A is reverseable
	 * @param solenoidAChannel
	 * @param solenoidAReverseChannel
	 * @param solenoidBChannel
	 * @param solenoidCChannel
	 */
	public TripleSolenoid(int solenoidAChannel, int solenoidAReverseChannel, int solenoidBChannel,
			int solenoidCChannel) {
		this(solenoidAChannel, solenoidAReverseChannel, 0, solenoidBChannel, 0, solenoidCChannel, 0);
	}
	
	/**
	 * Declares a Triple solenoid using 3 pneumatic channels, where every channel has a different pneumatic module.
	 * @param solenoidAChannel Connector closest to the 'head' of the pneumatic, the valve that supplies air.
	 * @param solenoidAReverseChannel Connector closet to the 'head' of the pneumatic, the valve that retracts air.
	 * @param solenoidAModule The module that the pneumatic is connected to.
	 * @param solenoidBChannel The middle connector on the triple solenoid.
	 * @param solenoidBModule The module associated with the middle connector.
	 * @param solenoidCChannel The connector closest to the 'butt' of the triple solenoid.
	 * @param solenoidCModule The module that the connector closest to the 'butt' of the triple sloenoid is connected to.
	 */
	public TripleSolenoid(int solenoidAChannel, int solenoidAReverseChannel, int solenoidAModule, int solenoidBChannel,
			int solenoidBModule, int solenoidCChannel, int solenoidCModule) {
		solenoidA = new DoubleSolenoid(solenoidAModule, solenoidAChannel, solenoidAReverseChannel);
		solenoidB = new Solenoid(solenoidBModule, solenoidBChannel);
		solenoidC = new Solenoid(solenoidCModule, solenoidCChannel);
	}

	/**
	 * An 'enumerator' for setting the position of the TripleSolenoid.
	 *
	 */
	public enum Position {
		RETRACT, MID, EXTEND
	}
	
	//A variable to hold the position of the TripleSolenoid, with the proper data type defined before - Position
	private Position m_position;
	
	/**
	 * Sets the individual direction of the TripleSolenoid, typically used for debugging.
	 * @param presurizeA True goes forward, false comes back.
	 * @param presurizeB True goes forward, false releases the flow/retracts.
	 * @param presurizeC True goes forward, false releases the flow/retracts.
	 */
	public void set(boolean presurizeA, boolean presurizeB, boolean presurizeC) {
		solenoidA.set(presurizeA?Value.kForward:Value.kReverse);
		solenoidB.set(presurizeB);
		solenoidC.set(presurizeC);
		System.out.println("triplesolenoid set: "+ presurizeA+","+presurizeB+","+presurizeC);
		Scheduler.getInstance().add(new DelayedCallback(0.125) {
			@Override
			public void onCallback() {
				solenoidA.set(Value.kOff);
			}
		});
	}
	
	/**
	 * Sets the airflow of the TripleSolenoid, typically used for normal operation.
	 * @param position RETRACT sets the pneumatic solenoids to pull the Solenoid back, MID is not accessable from RETRACT - only from EXTEND, and EXTEND extends the pneumatic solenoid out completely.
	 */
	public void set(Position position) {
		System.out.println(position.toString());
		switch (position) {
		case RETRACT:
			set(false, false, true);
			break;
		case MID:
			set(true, false, false);
			Scheduler.getInstance().add(new DelayedCallback(0.125) {
				@Override
				public void onCallback() {
					if (m_position == Position.MID)
						set(true, false, true);
				}
			});
			break;
		case EXTEND:
			set(true, true, false);
			break;
		default:
			throw new IllegalArgumentException();
		}
		m_position = position;
	}

	public Position getPosition() {
		return m_position;
	}
}
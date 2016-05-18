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

	public TripleSolenoid(int solenoidAChannel, int solenoidAReverseChannel, int solenoidAModule, int solenoidBChannel,
			int solenoidBModule, int solenoidCChannel, int solenoidCModule) {
		solenoidA = new DoubleSolenoid(solenoidAModule, solenoidAChannel, solenoidAReverseChannel);
		solenoidB = new Solenoid(solenoidBModule, solenoidBChannel);
		solenoidC = new Solenoid(solenoidCModule, solenoidCChannel);
	}

	public TripleSolenoid(int solenoidAChannel, int solenoidAReverseChannel, int solenoidBChannel,
			int solenoidCChannel) {
		this(solenoidAChannel, solenoidAReverseChannel, 0, solenoidBChannel, 0, solenoidCChannel, 0);
	}

	public enum Position {
		RETRACT, MID, EXTEND
	}

	private Position m_position;

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
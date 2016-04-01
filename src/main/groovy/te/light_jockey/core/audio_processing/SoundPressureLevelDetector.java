/*
*      _______                       _____   _____ _____  
*     |__   __|                     |  __ \ / ____|  __ \ 
*        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
*        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/ 
*        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |     
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|     
*                                                         
* -------------------------------------------------------------
*
* TarsosDSP is developed by Joren Six at IPEM, University Ghent
*  
* -------------------------------------------------------------
*
*  Info: http://0110.be/tag/TarsosDSP
*  Github: https://github.com/JorenSix/TarsosDSP
*  Releases: http://0110.be/releases/TarsosDSP/
*  
*  TarsosDSP includes modified source code by various authors,
*  for credits and info, see README.
* 
*/


package te.light_jockey.core.audio_processing;


import te.light_jockey.core.audio_processing.tarsos_dsp.AudioEvent;
import te.light_jockey.core.audio_processing.tarsos_dsp.AudioProcessor;

/**
 * An audio processor that detects the sound pressure level in dB for a given recording device
 */
public class SoundPressureLevelDetector implements AudioProcessor {

	private double currentSPL = 0;

	@Override
	public boolean process(AudioEvent audioEvent) {
		// Update the current sound pressure level reading (in dB)
		currentSPL = detectSoundPressureLevel(audioEvent.getFloatBuffer());

		//TODO: Figure out exactly what this does (via debug I'm guessing)
		// Returning true here ensures the audio stream from the given recording device is
		// repeatedly fed to to the chain of AudioProcessor(s) configured in the AudioDispatcher
		// that is controlling this processor.  Returning false here causes
		return true;
	}


	@Override
	public void processingFinished() {
		// NO-OP
	}

	/**
	 * Returns the dBSPL for a buffer.
	 *
	 * @param buffer The buffer with audio information.
	 * @return The dBSPL level for the buffer.
	 */
	private double detectSoundPressureLevel(final float[] buffer) {
		double value = Math.pow(localEnergy(buffer), 0.5);
		value = value / buffer.length;
		return linearToDecibel(value);
	}

	/**
	 * Calculates the local (linear) energy of an audio buffer.
	 *
	 * @param buffer The audio buffer.
	 * @return The local (linear) energy of an audio buffer.
	 */
	private double localEnergy(final float[] buffer) {
		double power = 0.0D;
		for (float element : buffer) {
			power += element * element;
		}
		return power;
	}

	/**
	 * Converts a linear to a dB value.
	 *
	 * @param value The value to convert.
	 * @return The converted value.
	 */
	private double linearToDecibel(final double value) {
		return 20.0 * Math.log10(value);
	}

	public double getCurrentSPL() {
		return currentSPL;
	}

}

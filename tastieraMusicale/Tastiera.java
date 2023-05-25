package tastieraMusicale;

import java.awt.*;
import java.awt.event.*;
import javax.sound.midi.*;
import javax.swing.*;

class Tastiera extends JFrame implements ActionListener {
	private JPanel pan = new JPanel();
	private JPanel panStrumenti = new JPanel();
	private JPanel panTasti = new JPanel();
	private JLabel lblStrumento = new JLabel("Strumento");
	private JComboBox cbStrumenti = new JComboBox();
	private JButton btnNota;

	private final String[] ARR_NOMI_NOTE = { "Do", "Re", "Mi", "Fa", "Sol", "La", "Si" };
	private final int[] ARR_NUM_MIDI = { 60, 62, 64, 65, 67, 69, 71 };

	private Synthesizer midiSynth = null;
	private Instrument[] arrStrumenti;
	private MidiChannel[] arrCanali;

	private String strumentoScelto;

	public Tastiera() {
		try {
			// crea e attiva il sintetizzatore
			midiSynth = MidiSystem.getSynthesizer();
			midiSynth.open();

			// legge i canali disponibili
			arrCanali = midiSynth.getChannels();

			// legge gli strumenti disponibili nell'archivio predefinito
			arrStrumenti = midiSynth.getDefaultSoundbank().getInstruments();
		} catch (MidiUnavailableException e) {
			System.out.println("Il sintetizzatore MIDI non può essere usato");
			System.exit(1);
		}

		// imposta l'elenco degli strumenti disponibili
		String nomeStrumento;
		for (int i = 0; i < arrStrumenti.length; i++) {
			nomeStrumento = arrStrumenti[i].getName();
			cbStrumenti.addItem(nomeStrumento);
		}

		// imposta il pannello degli strumenti
		panStrumenti.setLayout(new FlowLayout(FlowLayout.CENTER));
		panStrumenti.add(lblStrumento);
		panStrumenti.add(cbStrumenti);

		// imposta il pannello degli tasti
		panTasti.setLayout(new FlowLayout(FlowLayout.CENTER));
		for (int i = 0; i < ARR_NOMI_NOTE.length; i++) {
			btnNota = new JButton(ARR_NOMI_NOTE[i]);
			panTasti.add(btnNota);

			// assegna l'ascoltatore per l'evento
			btnNota.addActionListener(this);
		}

		// inserisce le componenti nel pannello principale
		pan.setLayout(new GridLayout(2, 1));
		pan.add(panStrumenti);
		pan.add(panTasti);

		// aggiunge il pannello al frame
		this.getContentPane().add(pan);

		// controlla l'uscita dal programma quando la finestra viene chiusa
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// gestione degli eventi
	public void actionPerformed(ActionEvent e) {
		int velocita = 100;
		int durata = 500;

		// legge la nota
		String strNota = e.getActionCommand();
		int idNota = 0;
		for (int i = 0; i < ARR_NOMI_NOTE.length; i++) {
			if (strNota.equals(ARR_NOMI_NOTE[i])) {
				idNota = ARR_NUM_MIDI[i];
				break;
			}
		}

		// legge lo strumento
		int idStrumento = cbStrumenti.getSelectedIndex();

		// carica lo strumento nel sintetizzatore
		midiSynth.loadInstrument(arrStrumenti[idStrumento]);

		// cambia lo strumento sul canale 0
		int idProgramma = arrStrumenti[idStrumento].getPatch().getProgram();
		arrCanali[0].programChange(idProgramma);

		// suona la nota sul canale 0
		arrCanali[0].noteOn(idNota, velocita);

		// interrompe la nota dopo una certo tempo
		try {
			Thread.sleep(durata);
		} catch (InterruptedException ex) {
		}

		arrCanali[0].noteOff(idNota);
	}
}

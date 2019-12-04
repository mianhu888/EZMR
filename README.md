# EZMR

Eric and Rommy are in charge of this project. We both are grad student in University of Delaware, CIS department. This is the implementation part for HCI final project, it's not done yet, but it has already implemented the ideal function. You can find our detailed introduction of this project here: https://sites.google.com/udel.edu/ezmr/home


We mainly have six functional activities. It starts from the MainActivity, and three buttons pointing to three activities, ChooseSongActivity followed by PracticeActivity, FreestyleActivity, and JournalActivity. From Practice and Freestyle it points to SettingActivity.

We use Bravura music font to display the sheet music. You can find more information here: https://www.smufl.org/fonts/

We use TarsosDSP Java library for audio processing. You can find more information here: https://github.com/JorenSix/TarsosDSP

Firstly, we define the attribute of different note, it has name, duration, frequencyInHz, is Tone or not, its unicode for Bravura font, and the length of unicode. We handle these attributes as JavaBean class and Json string. A specific example is: 
"do":
{
		"duration":0.75,
		"isTone":true,
		"length":2,
		"toneFreqInHz":261.6,
		"unicode":"_uEB9D_u+E1D5"
}

We typed in the sheet music we want, which is Little Star in this application. It looks like this: "spaceone","do","spaceone","do","spaceone","sol","spaceone","sol","spacetwo","barlineSingle","spaceone","la","spaceone","la","spaceone","sol","spaceone","sol","spacetwo","barlineSingle". 

Then we convert the music sheet to unicode to display it through Bravura font. By using Json and Jave bean, it looks like this: "u_E01A\\u_E050\\u_E014\\u_E09E\\u_E084\\u_E09F\\u_E084\\u_E01A\\u_EB9D\\u_E0A4\\u_EB9D\\u_E210\\u_E01A\\u_EB9D\\u_E0A4".

Then initialize all buttons, all variables, and all other things it needed. Several Thread is registered to buttons, generateThread, playThread, playbackThread. The generateThread is for detecting pitchs, the playThread is for playing the original sheet music, the playbackThread is for playing what the application have recorded when you were practicing. 

For playing, we generate the playing list according to "isTone" attribute of every notes in the sheet music. If this note is just for displaying, such as clef and barline, the "isTone" is "false" and we do not put it in the playing list. Then we play the song according to its frequency in Hz.

For detecting, TarsosDSP detects pitchs several times per second, we just pick one pitch every second. It is not the most efficient or best solution for a perfect application, but it's enough for the prototype. Then we compare pitchs we pick with the original note, to see if it is correct. If yes, color the generated note in black, if no, then in red.

For playback, we store the record as wav file, then our app can easily play it if you want.

When we leave this page, just clean up everything like a little smart programmer.

Also, the code is not concise, we use a lot of global variables and if-else judgement. If we have time, we will try to make it beautiful. 

Also and also, if you have any questions or ideas, you can simply shoot us an email, which is ericzh@udel.edu. We will be very appreciate it. 

const UUIDtoPlayer = (uuid) => {
	switch (uuid) {
		case "14d285df-e64e-41f2-bc4b-979e846c3cec":
			return {
				name: "SeaWhistle49963",
				color: "blue"
			}
		case "7d80f280-eaa6-404c-8830-643ccb357b62":
			return {
				name: "SapphireDoor632",
				color: "red"
			}
		case "ffaa5663-850e-4009-80c4-c8bbe34cd285":
			return {
				name: "ReservedCanoe67",
				color: "yellow"
			}
		case "6dc38184-c3e7-49ab-a99b-799b01274d01":
			return {
				name: "LeapingPage211",
				color: "cyan"
			}
		default:
			return {
				name: "unknown",
				color: "black"
			}
	}
}
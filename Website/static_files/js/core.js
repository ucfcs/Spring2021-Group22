async function getData(resourceName) {
    const raw = await fetch('/data/' + resourceName);
    return await raw.json();
}

teams = null;

async function fetchTeams() {
    teams = await getData('_teams.json')
}


function renderDropdown() {
    const element = $('#team-select')
    for (teamData of data) {
        element.append(
            $(
                '<button onclick="renderCharts(\'' +
                teamData.experiment_label +
                '\')">' +
                teamData.experiment_label +
                '</button>'
            )
        )
    }
}
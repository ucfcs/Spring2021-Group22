
async function renderTimeOfPlayerByLocationColumnChart(elements) {
  const data = (await getData('time_in_location_pie_chart.json')).charts

  for (let i = 0; i < data.length; i++) {
    const element = elements[i];
    const chart = data[i];
    const options = {
      title: {
        text: chart.title,
      },
      chart: {
        type: 'pie',
      },
      series: chart.series,
      labels: chart.labels,
      dataLabels: {
        enabled: false,
      },
    }
    
    new ApexCharts(element, options).render();
  }
}

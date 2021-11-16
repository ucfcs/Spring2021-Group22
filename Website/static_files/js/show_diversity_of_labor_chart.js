async function renderDiversityOfLaborColumnChart(elements) {
  const data = (await getData('diversity_of_labor_by_player_pie_charts.json')).charts

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

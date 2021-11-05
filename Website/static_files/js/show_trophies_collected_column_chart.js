async function renderTrophiesCollectedColumnChart(element) {
  const data = await getData('trophy_count_column_chart.json')
  
  const options = {
    title: {
      text: 'Trophies Collected',
    },
    chart: {
      type: 'bar',
      height: 350,
    },
    series: data.series,
    xaxis: {
      categories: data.categories,
    },
    yaxis: {
      title: {
        text: 'Count',
      },
    },
  }
  
  new ApexCharts(element, options).render();
}

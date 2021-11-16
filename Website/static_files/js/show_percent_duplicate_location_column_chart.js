async function renderPercentDuplicateLocationColumnChart(element) {
  const data = await getData('percent_duplicate_location_column_chart.json')
  
  const options = {
    title: {
      text: 'Percent of New Locations That Were Already Visited',
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
        text: 'Percent',
      },
    },
  }
  
  new ApexCharts(element, options).render();
}

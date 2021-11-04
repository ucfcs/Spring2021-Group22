async function renderDiversityOfLaborColumnChart(element) {
  const data = await getData('diversity_of_labor_column_chart.json')

  const options = {
    title: {
      text: 'Diversity of Labor by Players',
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
        text: 'Points',
      },
      decimalsInFloat: 2,
    },
    dataLabels: {
      enabled: false
    },
  }
  
  new ApexCharts(element, options).render();
}

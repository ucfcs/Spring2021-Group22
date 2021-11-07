async function renderSpecialItemsOverTimeUnweighted(element) {
  const data = await getData('special_items_over_time_unweighted.json')

  const options = {
    series: data.series,
    chart: {
      height: 350,
      type: 'line',
      zoom: {
        type: 'x',
        enabled: true,
        autoScaleYaxis: true
      },
    },
    dataLabels: {
      enabled: false,
    },
    title: {
      text: 'Special Item Usage Over Time',
      align: 'left',
    },
    grid: {
      row: {
        colors: ['#f3f3f3', 'transparent'],
        opacity: 0.5,
      },
    },
    xaxis: {
      categories: data.categories,
    },
  }

  new ApexCharts(element, options).render()
}

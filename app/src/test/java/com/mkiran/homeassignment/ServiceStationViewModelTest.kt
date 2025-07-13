package com.mkiran.homeassignment
 
import com.mkiran.homeassignment.domain.model.ServiceStationDto
import com.mkiran.homeassignment.domain.repository.ServiceStationRepository
import com.mkiran.homeassignment.presentation.ServiceStationUiState
import com.mkiran.homeassignment.presentation.ServiceStationViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ServiceStationViewModelTest {
    private lateinit var repository: ServiceStationRepository
    private lateinit var viewModel: ServiceStationViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val station1 = ServiceStationDto(
        id = "1",
        name = "Station 1",
        address = "Addr 1",
        latitude = 0.0,
        longitude = 0.0,
        distanceMiles = 1.0,
        isOpen24Hours = true,
        hasConvenienceStore = true,
        hasHotFood = false,
        acceptsBpFuelCards = true,
        imageUrl = null
    )
    private val station2 = ServiceStationDto(
        id = "2",
        name = "Station 2",
        address = "Addr 2",
        latitude = 0.0,
        longitude = 0.0,
        distanceMiles = 6.0,
        isOpen24Hours = false,
        hasConvenienceStore = false,
        hasHotFood = true,
        acceptsBpFuelCards = false,
        imageUrl = null
    )
    private val allStations = listOf(station1, station2)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        coEvery { repository.getServiceStations() } returns allStations
        coEvery { repository.getCachedServiceStations() } returns allStations
        viewModel = ServiceStationViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct and fetchStations loads data`() = testScope.runTest {
        // Initial filter state
        assertEquals(5.0, viewModel.filter.value.radius, 0.0)
        assertEquals(false, viewModel.filter.value.isOpen24Hours)
        assertEquals(false, viewModel.filter.value.hasConvenienceStore)
        assertEquals(false, viewModel.filter.value.hasHotFood)
        assertEquals(false, viewModel.filter.value.acceptsBpFuelCards)
        // After fetchStations (called in init)
        testDispatcher.scheduler.advanceUntilIdle()
        val uiState = viewModel.uiState.value
        assert(uiState is ServiceStationUiState.Success)
        val stations = (uiState as ServiceStationUiState.Success).stations
        assertEquals(listOf(station1), stations) // Only station1 within 5 miles
    }

    @Test
    fun `setRadius updates radius and filters stations`() = testScope.runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.setRadius(10.0)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(10.0, viewModel.filter.value.radius, 0.0)
        val uiState = viewModel.uiState.value
        assert(uiState is ServiceStationUiState.Success)
        val stations = (uiState as ServiceStationUiState.Success).stations
        assertEquals(allStations, stations)
    }

    @Test
    fun `setIsOpen24Hours filters stations`() = testScope.runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.setIsOpen24Hours(true)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(true, viewModel.filter.value.isOpen24Hours)
        val uiState = viewModel.uiState.value
        assert(uiState is ServiceStationUiState.Success)
        val stations = (uiState as ServiceStationUiState.Success).stations
        assertEquals(listOf(station1), stations)
    }

    @Test
    fun `setHasConvenienceStore filters stations`() = testScope.runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.setHasConvenienceStore(true)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(true, viewModel.filter.value.hasConvenienceStore)
        val uiState = viewModel.uiState.value
        assert(uiState is ServiceStationUiState.Success)
        val stations = (uiState as ServiceStationUiState.Success).stations
        assertEquals(listOf(station1), stations)
    }

    @Test
    fun `setHasHotFood filters stations`() = testScope.runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.setHasHotFood(true)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(true, viewModel.filter.value.hasHotFood)
        val uiState = viewModel.uiState.value
        // Only station2 has hot food, but it's outside default radius
        assert(uiState is ServiceStationUiState.Empty)
        viewModel.setRadius(10.0)
        testDispatcher.scheduler.advanceUntilIdle()
        val uiState2 = viewModel.uiState.value
        assert(uiState2 is ServiceStationUiState.Success)
        val stations = (uiState2 as ServiceStationUiState.Success).stations
        assertEquals(listOf(station2), stations)
    }

    @Test
    fun `setAcceptsBpFuelCards filters stations`() = testScope.runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.setAcceptsBpFuelCards(true)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(true, viewModel.filter.value.acceptsBpFuelCards)
        val uiState = viewModel.uiState.value
        assert(uiState is ServiceStationUiState.Success)
        val stations = (uiState as ServiceStationUiState.Success).stations
        assertEquals(listOf(station1), stations)
    }

    @Test
    fun `fetchStations handles repository error and uses cache`() = testScope.runTest {
        coEvery { repository.getServiceStations() } throws RuntimeException("Network error")
        coEvery { repository.getCachedServiceStations() } returns listOf(station1)
        viewModel.fetchStations()
        testDispatcher.scheduler.advanceUntilIdle()
        val uiState = viewModel.uiState.value
        assert(uiState is ServiceStationUiState.Success)
        val stations = (uiState as ServiceStationUiState.Success).stations
        assertEquals(listOf(station1), stations)
    }

    @Test
    fun `fetchStations handles error and empty cache`() = testScope.runTest {
        coEvery { repository.getServiceStations() } throws RuntimeException("Network error")
        coEvery { repository.getCachedServiceStations() } returns emptyList()
        viewModel.fetchStations()
        testDispatcher.scheduler.advanceUntilIdle()
        val uiState = viewModel.uiState.value
        assert(uiState is ServiceStationUiState.Error)
        val message = (uiState as ServiceStationUiState.Error).message
        assertEquals("Network error", message)
    }

    @Test
    fun `loading state is set correctly during fetchStations`() = testScope.runTest {
        coEvery { repository.getServiceStations() } coAnswers {
            testDispatcher.scheduler.advanceTimeBy(100)
            allStations
        }
        viewModel.fetchStations()
        // Immediately after fetchStations, should be loading
        val uiState = viewModel.uiState.value
        assert(uiState is ServiceStationUiState.Loading)
        testDispatcher.scheduler.advanceUntilIdle()
        val uiState2 = viewModel.uiState.value
        assert(uiState2 is ServiceStationUiState.Success)
    }
}